package com.example.weather.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean allowRequest(String apiKey) {
        // Create a new bucket for each API key
        Bucket bucket = buckets.computeIfAbsent(apiKey, this::createNewBucket);
        return bucket.tryConsume(1);
    }

    Bucket createNewBucket(String apiKey) {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(10, Duration.ofMinutes(60)));
        return Bucket4j.builder().addLimit(limit).build();
    }
}
