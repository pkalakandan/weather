package com.example.weather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitingServiceTest {

    private RateLimitingService rateLimitingService;

    @BeforeEach
    public void setUp() {
        rateLimitingService = new RateLimitingService();
    }

    @Test
    void testAllowRequest() {
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
    }

    @Test
    void testRejectRequest() {
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertFalse(rateLimitingService.allowRequest("key1"));
    }

    @Test
    void testRejectRequestMultipleKeys() {
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));
        assertTrue(rateLimitingService.allowRequest("key1"));

        assertTrue(rateLimitingService.allowRequest("key2"));
        assertTrue(rateLimitingService.allowRequest("key2"));
        assertTrue(rateLimitingService.allowRequest("key2"));
        assertTrue(rateLimitingService.allowRequest("key2"));
        assertTrue(rateLimitingService.allowRequest("key2"));

        assertFalse(rateLimitingService.allowRequest("key1"));
        assertFalse(rateLimitingService.allowRequest("key2"));
    }
}