package com.example.WheaterApp.weatherAPI;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private static final String API_KEY = "your-api-key";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

//    @Autowired
//    private RestTemplate restTemplate;
//
//    public WeatherResponse getWeather(String cityName) {
//        String url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, cityName, API_KEY);
//        ResponseEntity<WeatherResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, WeatherResponse.class);
//        return response.getBody();
//    }
}

