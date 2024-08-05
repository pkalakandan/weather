package com.example.weather.controller;

import com.example.weather.config.WithRateLimitProtection;
import com.example.weather.model.RateLimitException;
import com.example.weather.model.request.WeatherForecastRequest;
import com.example.weather.model.response.WeatherForecastResponse;
import com.example.weather.service.RateLimitingService;
import com.example.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

import static com.example.weather.util.Constants.HEADER_API_KEY;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private static final Logger LOGGER = Logger.getLogger(WeatherController.class.getName());

    private final WeatherService weatherService;
    private final RateLimitingService rateLimitingService;

    public WeatherController(WeatherService weatherService, RateLimitingService rateLimitingService) {
        this.weatherService = weatherService;
        this.rateLimitingService = rateLimitingService;
    }

    // annotation api key from header
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved weather"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @GetMapping(path = "/{country}/{city}")
    @WithRateLimitProtection
    public Mono<WeatherForecastResponse> getWeather(
            @ParameterObject @Valid WeatherForecastRequest weatherForecastRequest,
            @RequestHeader(HEADER_API_KEY) String apiKey
    ) {
        if (rateLimitingService.allowRequest(apiKey)) {
            LOGGER.info("Getting weather");
            return weatherService.getWeather(weatherForecastRequest.getCountry(), weatherForecastRequest.getCity());
        } else {
            // return 429 Too Many Requests status code
            throw new RateLimitException("Rate limit exceeded");
        }

    }
}
