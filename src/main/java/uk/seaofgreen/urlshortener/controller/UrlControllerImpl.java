package uk.seaofgreen.urlshortener.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.seaofgreen.urlshortener.entity.Url;
import uk.seaofgreen.urlshortener.service.UrlService;

import java.util.List;
import java.util.Optional;

// TODO - "better" validate incoming data
// TODO - CAREFULLY check all of this class via API spec - tests cases for ALL scenarios
// TODO - javadoc
@RestController
public class UrlControllerImpl implements UrlController {
    private final UrlService urlService;

    public UrlControllerImpl(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    @Override
    public ResponseEntity<ShortenUrlResponse> shorten(@Valid @RequestBody ShortenUrlRequest request,
                                                       @RequestHeader(value = "Host") String host) {
        try {
            Url url = urlService.createUrl(
                    request.url(),
                    Optional.ofNullable(request.alias())
            );

            String shortUrl = "http://" + host + "/" + url.getAlias(); // TODO - https?
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ShortenUrlResponse(url.getAlias(), url.getFullUrl(), shortUrl));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{alias}")
    @Override
    public void redirect(@PathVariable String alias, HttpServletResponse response) {
        Url url = urlService.getUrl(alias)
                .orElseThrow(() -> new RuntimeException("Alias not found")); // TODO - fix to correct status

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", url.getFullUrl());
    }

    @DeleteMapping("/{alias}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable String alias) {
        if (urlService.getUrl(alias).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        urlService.deleteUrl(alias);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/urls")
    @Override
    public List<ShortenUrlResponse> list(@RequestHeader(value = "Host") String host) {
        return urlService.getAllUrls().stream()
                .map(u -> new ShortenUrlResponse(
                        u.getAlias(),
                        u.getFullUrl(),
                        "http://" + host + "/" + u.getAlias()
                ))
                .toList();
    }

    // DTOs
    public record ShortenUrlRequest(
            @NotBlank String url,
            String alias
    ) {}

    public record ShortenUrlResponse(
            String alias,
            String fullUrl,
            String shortUrl
    ) {}
}
