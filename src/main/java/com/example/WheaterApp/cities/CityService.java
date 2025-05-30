package com.example.WheaterApp.cities;

import com.example.WheaterApp.appuser.AppUser;
import com.example.WheaterApp.appuser.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private final UserRepository userRepository;
    private final CityRepository cityRepository;

    public CityService(UserRepository userRepository, CityRepository cityRepository) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
    }
    public List<City> getFavorites(String userEmail) {
        AppUser user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        return cityRepository.findByUser(user);
    }

    public void addFavorite(String userEmail, String name) {
        AppUser user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalStateException("User not found")); // Folosim IllegalStateException pentru a fi consistent cu GlobalExceptionHandler

        Optional<City> existingCity = cityRepository.findByNameAndUser(name, user);
        if (existingCity.isPresent()) {
            throw new IllegalStateException("City '" + name + "' is already in favorites for this user.");
        }

        City city = new City(name, user);
        cityRepository.save(city);
    }
    public void removeFavorite(String cityName, String userEmail) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // find the city by name AND user
        City city = cityRepository.findByNameAndUser(cityName, user)
                .orElseThrow(() -> new RuntimeException("City not found for this user"));

        cityRepository.delete(city);
    }
}
