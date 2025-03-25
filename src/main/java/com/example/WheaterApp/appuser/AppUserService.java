package com.example.WheaterApp.appuser;

import com.example.WheaterApp.registration.token.ConfirmationToken;
import com.example.WheaterApp.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MSG="User with email: %s not found.";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    @Autowired
    public AppUserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,ConfirmationTokenService confirmationTokenService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder= bCryptPasswordEncoder;
        this.confirmationTokenService=confirmationTokenService;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,email)));
    }

    public String signUpUser(AppUser appUser){
        boolean userExists = userRepository.findByEmail(appUser.getEmail()).isPresent();
        if(userExists){
            //TODO: check if attributes are the same and if email not confirmed send confirmation email
            Optional<AppUser> existingUserOptional = userRepository.findByEmail(appUser.getEmail());
            AppUser existingUser = existingUserOptional.get();
            if (existingUser.getEnabled()) {
                throw new IllegalStateException("Email already taken.");
            }
            boolean sameAttributes = existingUser.getFirstName().equals(appUser.getFirstName()) &&
                    existingUser.getLastName().equals(appUser.getLastName()) &&
                    existingUser.getEmail().equals(appUser.getEmail()) && existingUser.getPassword().equals(appUser.getPassword());
            if(sameAttributes){
                String token = UUID.randomUUID().toString();
                ConfirmationToken confirmationToken= new ConfirmationToken(token, LocalDateTime.now(),LocalDateTime.now().plusMinutes(15),existingUser);
                confirmationTokenService.saveConfirmationToken(confirmationToken);
                return token;
            }
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        userRepository.save(appUser);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken= new ConfirmationToken(token, LocalDateTime.now(),LocalDateTime.now().plusMinutes(15),appUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }
    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }
}
