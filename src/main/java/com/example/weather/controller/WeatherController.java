package com.example.weather.controller;

import com.example.weather.model.WeatherResponse;
import com.example.weather.model.request.WeatherForecastRequest;
import com.example.weather.model.response.WeatherForecastResponse;
import com.example.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private static final Logger LOGGER = Logger.getLogger(WeatherController.class.getName());

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved maxFeelsLike and maxHumidity"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "City value and authorizationHeader can not be null")
    })
    @GetMapping(path = "/get/{country}/{city}")
    public Mono<WeatherForecastResponse> getWeather(
            @ParameterObject @Valid WeatherForecastRequest weatherForecastRequest
    ) {
        LOGGER.info("Getting weather");
        return weatherService.getWeather(weatherForecastRequest.getCountry(), weatherForecastRequest.getCity());
    }
}
