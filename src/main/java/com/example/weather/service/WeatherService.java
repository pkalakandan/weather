package com.example.weather.service;

import com.example.weather.model.Geo;
import com.example.weather.model.GeoResponse;
import com.example.weather.model.WeatherResponse;
import com.example.weather.model.response.WeatherForecastResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.logging.Logger;

@Service
public class WeatherService {
    private static final Logger logger = Logger.getLogger(WeatherService.class.getName());

    private final WebClient webClient;
    private final String API_KEY;
    private final String API_WEATHER_URL;
    private final String API_GEO_URL;

    public WeatherService(Environment environment, WebClient webClient) {
        this.webClient = webClient;
        this.API_KEY = environment.getProperty("openweathermap.api.key");
        this.API_WEATHER_URL = environment.getProperty("openweathermap.api.weather.url");
        this.API_GEO_URL = environment.getProperty("openweathermap.api.geo.url");
    }

    public Mono<WeatherForecastResponse> getWeather(String country, String city) {
        logger.info("Getting weather");

        return getGeoLocation(country, city)
                .flatMap(geoResponse -> getWeatherByLatLon(geoResponse[0].getLat(),
                        geoResponse[0].getLon()))
                .map(weatherResponse -> new WeatherForecastResponse(weatherResponse.getWeather().get(0).getDescription()));
    }

    private Mono<WeatherResponse> getWeatherByLatLon(double lat, double lon) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_WEATHER_URL)
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(WeatherResponse.class);
    }

    private Mono<Geo[]> getGeoLocation(String country, String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_GEO_URL)
                        .queryParam("q", city + "," + country)
                        .queryParam("appid", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(Geo[].class);
    }
}
