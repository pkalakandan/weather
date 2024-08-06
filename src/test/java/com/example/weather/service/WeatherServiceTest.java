package com.example.weather.service;

import com.example.weather.model.Geo;
import com.example.weather.model.WeatherResponse;
import com.example.weather.model.response.WeatherForecastResponse;
import com.example.weather.repository.WeatherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class WeatherServiceTest {

    private static final MockWebServer mockWebServer = new MockWebServer();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private WeatherService weatherService;

    private final WebClient webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build();
    private final WeatherRepository weatherRepository = Mockito.mock(WeatherRepository.class);

    static void setUp() throws IOException {
        mockWebServer.start();
    }

    @BeforeEach
    void setup() {
        Environment environment = Mockito.mock(Environment.class);
        Mockito.when(environment.getProperty("openweathermap.api.key")).thenReturn("test");
        Mockito.when(environment.getProperty("openweathermap.api.weather.url")).thenReturn(mockWebServer.url("/").toString());
        Mockito.when(environment.getProperty("openweathermap.api.geo.url")).thenReturn(mockWebServer.url("/").toString());
        weatherService = new WeatherService(environment, webClient, weatherRepository);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    public void testGeo() throws IOException, InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(readTestFileAsString("expectedGeoResponse.json")));

        Geo[] response = weatherService.getGeoLocation("au", "melbourne").block();
        assertAll(
                () -> assertEquals(-37.8142176, response[0].getLat()),
                () -> assertEquals(144.9631608, response[0].getLon())
        );


        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), containsString("melbourne,au"));
    }

    @Test
    public void testWeather() throws IOException, InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(readTestFileAsString("expectedWeatherResponse.json")));

        WeatherResponse response = weatherService.getWeatherByLatLon(48.3059078, 14.286198).block();
        assertAll(
                () -> assertEquals("broken clouds", response.getWeather().get(0).getDescription())
        );


        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), containsString("lat=48.3059078&lon=14.286198"));
    }

    @Test
    public void testGetWeather() throws IOException, InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(readTestFileAsString("expectedGeoResponse.json")));

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(readTestFileAsString("expectedWeatherResponse.json")));

        WeatherForecastResponse response = weatherService.getWeather("AU", "Melbourne").block();
        assertAll(
                () -> assertEquals("broken clouds", response.getDescription())
        );

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), containsString("Melbourne,AU"));

        recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), containsString("lat=-37.8142176&lon=144.9631608"));
    }


    private String readTestFileAsString(String relativeFilePath) throws IOException {
        return new String(new ClassPathResource(relativeFilePath).getInputStream().readAllBytes());
    }
}