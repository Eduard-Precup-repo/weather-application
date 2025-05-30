package com.example.WheaterApp.cities;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/favorite-cities")
@CrossOrigin(origins = "http://localhost:5173")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<List<City>> getFavorites() {
        try {
            String userEmail = getCurrentUserEmail();
            //System.out.println("DEBUG: CityController - Getting favorites for user: " + userEmail);
            List<City> favorites = cityService.getFavorites(userEmail);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            System.err.println("ERROR: CityController - Error getting favorites: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<String> addFavorite(@RequestBody Map<String, String> body) {
        try {
            String cityName = body.get("cityName");
            String userEmail = getCurrentUserEmail();
            //System.out.println("DEBUG: CityController - Adding favorite city '" + cityName + "' for user: " + userEmail);

            if (cityName == null || cityName.trim().isEmpty()) {
                System.err.println("ERROR: CityController - City name is null or empty in request body.");
                return ResponseEntity.badRequest().body("City name cannot be empty.");
            }

            cityService.addFavorite(userEmail, cityName);
            return ResponseEntity.ok("City added to favorites!");
        } catch (Exception e) {
            System.err.println("ERROR: CityController - Error adding favorite: " + e.getMessage());
            throw e;
        }
    }

    @DeleteMapping
    public ResponseEntity<String> removeFavorite(@RequestBody Map<String, String> body) {
        try {
            String cityName = body.get("cityName");
            String userEmail = getCurrentUserEmail();
            //System.out.println("DEBUG: CityController - Removing favorite city '" + cityName + "' for user: " + userEmail);

            if (cityName == null || cityName.trim().isEmpty()) {
                System.err.println("ERROR: CityController - City name is null or empty in request body for removal.");
                return ResponseEntity.badRequest().body("City name cannot be empty for removal.");
            }

            cityService.removeFavorite(cityName, userEmail);
            return ResponseEntity.ok("City removed from favorites!");
        } catch (Exception e) {
            System.err.println("ERROR: CityController - Error removing favorite: " + e.getMessage());
            throw e;
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            System.err.println("ERROR: getCurrentUserEmail - Authentication object is null in SecurityContextHolder!");
            throw new IllegalStateException("User not authenticated for this operation.");
        }
        //System.out.println("DEBUG: getCurrentUserEmail - Authentication found. Name: " + authentication.getName());
        return authentication.getName(); // Assuming username = email in JWT
    }
}