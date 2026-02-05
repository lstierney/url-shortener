package com.tpximpact.urlshortener.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tpximpact.urlshortener.entity.Url;
import com.tpximpact.urlshortener.service.UrlService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UrlControllerImpl implements UrlController {
    private static final Logger log = LoggerFactory.getLogger(UrlControllerImpl.class);
    private final UrlService urlService;

    public UrlControllerImpl(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    @Override
    public ResponseEntity<ShortenUrlResponse> shorten(
            @Valid @RequestBody ShortenUrlRequest request,
            @RequestHeader("Host") String host) {

        try {
            Url url = urlService.createUrl(
                    request.fullUrl(),
                    Optional.ofNullable(request.customAlias())
            );

            String shortUrl = "http://" + host + "/" + url.getAlias();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ShortenUrlResponse(shortUrl));
        } catch (IllegalArgumentException e) {
            log.info("Request alias: {} already in use", request.customAlias());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{alias}")
    @Override
    public ResponseEntity<Void> redirect(@PathVariable String alias) {
        var url = urlService.getUrl(alias);
        if (url.isEmpty()) {
            log.info("Requested alias: {} not found", alias);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url.get().getFullUrl())
                .build();
    }

    @DeleteMapping("/{alias}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable String alias) {
        if (urlService.getUrl(alias).isEmpty()) {
            log.info("Requested alias: {} not found", alias);
            return ResponseEntity.notFound().build();
        }

        urlService.deleteUrl(alias);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/urls")
    @Override
    public List<ShortenUrlListResponse> list(@RequestHeader("Host") String host) {
        return urlService.getAllUrls().stream()
                .map(u -> new ShortenUrlListResponse(
                        u.getAlias(),
                        u.getFullUrl(),
                        "http://" + host + "/" + u.getAlias()
                ))
                .toList();
    }

    // DTOs
    public record ShortenUrlRequest(
            // You shall not pass!! Throws 400/BAD_REQUEST on validation failure
            @URL(message = "fullUrl must be a valid URL")
            @NotBlank
            String fullUrl,

            @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "customAlias may only contain letters, numbers, hyphens, and underscores")
            @Size(max = 20, message = "customAlias must be at most 20 characters")
            String customAlias

    ) {}

    public record ShortenUrlResponse(
            String shortUrl
    ) {}

    public record ShortenUrlListResponse(
            String alias,
            String fullUrl,
            String shortUrl
    ) {}
}
