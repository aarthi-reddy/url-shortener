package com.aarthi.urlshortener.service;

import com.aarthi.urlshortener.entity.UrlMapping;
import com.aarthi.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisService redisService;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    public UrlMapping shortenUrl(String originalUrl) {
        String shortCode = generateUniqueShortCode();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortCode(shortCode);
        urlMapping.setCreatedAt(LocalDateTime.now());
        urlMapping.setClickCount(0L);

        UrlMapping saved = urlRepository.save(urlMapping);

        // Cache in Redis immediately after saving
        redisService.cacheUrl(shortCode, originalUrl);

        return saved;
    }

    public Optional<UrlMapping> getOriginalUrl(String shortCode) {
        // 1. Check Redis cache first
        String cachedUrl = redisService.getCachedUrl(shortCode);
        if (cachedUrl != null) {
            System.out.println("Cache HIT for: " + shortCode);
            UrlMapping cached = new UrlMapping();
            cached.setShortCode(shortCode);
            cached.setOriginalUrl(cachedUrl);
            return Optional.of(cached);
        }

        // 2. Cache MISS - go to MySQL
        System.out.println("Cache MISS for: " + shortCode);
        Optional<UrlMapping> urlMapping = urlRepository.findByShortCode(shortCode);
        urlMapping.ifPresent(mapping -> {
            mapping.setClickCount(mapping.getClickCount() + 1);
            urlRepository.save(mapping);
            // Store in Redis for next time
            redisService.cacheUrl(shortCode, mapping.getOriginalUrl());
        });

        return urlMapping;
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (urlRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}