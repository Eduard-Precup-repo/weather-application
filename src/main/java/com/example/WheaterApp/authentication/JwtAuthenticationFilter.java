package com.example.WheaterApp.authentication;

import com.example.WheaterApp.appuser.AppUser;
import io.jsonwebtoken.ExpiredJwtException; // Importă această clasă
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importă această clasă
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String username = null; // Inițializăm username la null

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            //System.out.println("DEBUG: No Bearer token found or Authorization header missing. Proceeding unauthenticated.");
            filterChain.doFilter(request, response);
            //System.out.println("--- DEBUG END JwtAuthenticationFilter (No Token) ---\n");
            return;
        }

        jwt = authHeader.substring(7);
        //System.out.println("DEBUG: Extracted JWT (partial): " + jwt.substring(0, Math.min(jwt.length(), 30)) + "...");

        try {
            username = jwtService.extractUsername(jwt);
            //System.out.println("DEBUG: Extracted username from JWT: " + username);
        } catch (ExpiredJwtException e) {
            System.err.println("ERROR: JWT token has expired for request to: " + request.getRequestURI() + " - " + e.getMessage());
            // Tokenul a expirat, nu setăm autentificarea.
            filterChain.doFilter(request, response);
            //System.out.println("--- DEBUG END JwtAuthenticationFilter (Expired Token) ---\n");
            return;
        } catch (Exception e) {
            System.err.println("ERROR: Error processing JWT for request to: " + request.getRequestURI() + " - " + e.getMessage());
            filterChain.doFilter(request, response); // Continuăm lanțul de filtre
            //System.out.println("--- DEBUG END JwtAuthenticationFilter (JWT Processing Error) ---\n");
            return;
        }

        // Doar dacă username-ul a fost extras cu succes și nu există deja o autentificare în context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //System.out.println("DEBUG: Username is not null and no existing authentication. Attempting to load user details...");
            AppUser userDetails = null;
            try {
                userDetails = (AppUser) this.userDetailsService.loadUserByUsername(username);
                System.out.println("DEBUG: UserDetails loaded for: " + (userDetails != null ? userDetails.getEmail() : "null"));
            } catch (UsernameNotFoundException e) {
                System.err.println("ERROR: User from JWT (" + username + ") not found in database for request to: " + request.getRequestURI());
                // Utilizatorul nu a fost găsit, nu setăm autentificarea.
            } catch (Exception e) {
                System.err.println("ERROR: Generic error loading user details for " + username + " - " + e.getMessage());
            }

            if (userDetails != null) {
                //System.out.println("DEBUG: Checking if token is valid and user is enabled/not locked...");
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Verificăm și starea contului
                    if (userDetails.isEnabled() && userDetails.isAccountNonLocked()) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("DEBUG: Authentication set successfully for user: " + userDetails.getEmail());
                        System.out.println("DEBUG: SecurityContextHolder now contains: " + SecurityContextHolder.getContext().getAuthentication().getName());
                    } else {
                        System.err.println("DEBUG: User " + username + " account is not enabled or is locked. Cannot authenticate.");
                    }
                } else {
                    System.err.println("DEBUG: JWT token invalid for user: " + username + ". Not setting authentication.");
                }
            } else {
                System.err.println("DEBUG: UserDetails is null. Authentication not set.");
            }
        } else {
            System.out.println("DEBUG: Username is null OR Authentication already exists (SecurityContextHolder.getAuthentication() is NOT null). Not attempting new authentication in filter.");
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                System.out.println("DEBUG: Existing Authentication: " + SecurityContextHolder.getContext().getAuthentication().getName());
            }
        }

        filterChain.doFilter(request, response);
        //System.out.println("--- DEBUG END JwtAuthenticationFilter (Chain Continued) ---\n");
    }
}