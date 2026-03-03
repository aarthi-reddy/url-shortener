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

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    public UrlMapping shortenUrl(String originalUrl) {
        String shortCode = generateUniqueShortCode();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortCode(shortCode);
        urlMapping.setCreatedAt(LocalDateTime.now());
        urlMapping.setClickCount(0L);

        return urlRepository.save(urlMapping);
    }

    public Optional<UrlMapping> getOriginalUrl(String shortCode) {
        Optional<UrlMapping> urlMapping = urlRepository.findByShortCode(shortCode);
        urlMapping.ifPresent(mapping -> {
            mapping.setClickCount(mapping.getClickCount() + 1);
            urlRepository.save(mapping);
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