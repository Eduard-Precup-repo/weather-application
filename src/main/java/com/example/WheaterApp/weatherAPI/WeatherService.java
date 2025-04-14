package com.example.WheaterApp.weatherAPI;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String GEO_API_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast";

    public String getWeather(String city) {
        try {
            // Step 1: Get city coordinates
            String geoUrl = UriComponentsBuilder.fromHttpUrl(GEO_API_URL)
                    .queryParam("name", city)
                    .queryParam("count", 1)  // Limit results to 1 city
                    .queryParam("format", "json")
                    .toUriString();

            String geoResponse = restTemplate.getForObject(geoUrl, String.class);
            JsonNode geoJson = objectMapper.readTree(geoResponse);

            if (geoJson.has("results") && geoJson.get("results").size() > 0) {
                double latitude = geoJson.get("results").get(0).get("latitude").asDouble();
                double longitude = geoJson.get("results").get(0).get("longitude").asDouble();

                // Step 2: Get weather data
                String weatherUrl = UriComponentsBuilder.fromHttpUrl(WEATHER_API_URL)
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current_weather", true)
                        .toUriString();

                return restTemplate.getForObject(weatherUrl, String.class);
            } else {
                return "{\"error\": \"City not found\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"An error occurred\"}";
        }
    }
}