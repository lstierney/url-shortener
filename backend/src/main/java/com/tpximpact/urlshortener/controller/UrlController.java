package com.tpximpact.urlshortener.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.io.IOException;
import java.util.List;

import static com.tpximpact.urlshortener.controller.UrlControllerImpl.*;

/**
 * Defines the HTTP endpoints for creating, resolving, deleting and listing shortened URLs.
 *
 * <p>The Host header is used to construct the externally visible short URL returned to clients.
 */
public interface UrlController {

    /**
     * Creates a shortened URL, optionally using a custom alias.
     */
    ResponseEntity<ShortenUrlResponse> shorten(@Valid @RequestBody ShortenUrlRequest request,
                                               @RequestHeader(value = "Host") String host);

    /**
     * Resolves an alias and issues an HTTP redirect to the original URL.
     */
    ResponseEntity<Void> redirect(@PathVariable String alias) throws IOException;

    /**
     * Deletes a previously created shortened URL.
     */
    ResponseEntity<Void> delete(@PathVariable String alias);

    /**
     * Returns all shortened URLs, including their fully resolved short form.
     */
    List<ShortenUrlListResponse> list(@RequestHeader(value = "Host") String host);
}

