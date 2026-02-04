package uk.seaofgreen.urlshortener.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.seaofgreen.urlshortener.entity.Url;
import uk.seaofgreen.urlshortener.service.UrlService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UrlControllerImpl implements UrlController {

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

            String shortUrl = "https://" + host + "/" + url.getAlias();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ShortenUrlResponse(shortUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{alias}")
    @Override
    public ResponseEntity<Void> redirect(@PathVariable String alias) {
        var url = urlService.getUrl(alias);
        if (url.isEmpty()) {
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
                        "https://" + host + "/" + u.getAlias()
                ))
                .toList();
    }

    // DTOs
    public record ShortenUrlRequest(
            @NotBlank String fullUrl,
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
