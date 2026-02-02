package uk.seaofgreen.urlshortener.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UrlController {
    ResponseEntity<UrlControllerImpl.ShortenUrlResponse> shorten(@Valid @RequestBody UrlControllerImpl.ShortenUrlRequest request,
                                                                 @RequestHeader(value = "Host") String host);

    void redirect(@PathVariable String alias, HttpServletResponse response) throws IOException;

    ResponseEntity<Void> delete(@PathVariable String alias);

    List<UrlControllerImpl.ShortenUrlResponse> list(@RequestHeader(value = "Host") String host);
}
