package com.aarthi.urlshortener.controller;

import com.aarthi.urlshortener.entity.UrlMapping;
import com.aarthi.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlMapping> shortenUrl(@RequestBody String originalUrl) {
        UrlMapping urlMapping = urlService.shortenUrl(originalUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(urlMapping);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<String> redirect(@PathVariable String shortCode) {
        Optional<UrlMapping> urlMapping = urlService.getOriginalUrl(shortCode);
        if (urlMapping.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", urlMapping.get().getOriginalUrl())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short URL not found");
    }
}