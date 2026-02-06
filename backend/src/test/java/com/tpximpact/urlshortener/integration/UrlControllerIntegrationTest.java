package com.tpximpact.urlshortener.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import org.springframework.test.context.ActiveProfiles;
import com.tpximpact.urlshortener.controller.UrlControllerImpl;
import com.tpximpact.urlshortener.controller.UrlControllerImpl.ShortenUrlResponse;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static com.tpximpact.urlshortener.testsupport.TestDataFactory.*;

/**
 * Full end‑to‑end integration tests using TestRestTemplate.<br/>
 * TestRestTemplate is used here because it exercises the application through
 * a real HTTP server, giving us confidence that routing, serialization,
 * validation, filters, exception handling and the controller layer all behave
 * exactly as they do in production. This makes it ideal for testing the
 * overall API contract rather than just controller logic.<br/>
 * Redirect behaviour is tested separately with MockMvc, as TestRestTemplate
 * automatically follows redirects and does not expose the raw 3xx response.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UrlControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("Host", HOST);
        return h;
    }

    @Test
    void shorten_success_201() {
        // When
        ResponseEntity<ShortenUrlResponse> response = createAlias();

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().shortUrl(), is(baseUrl() + "/" + ALIAS_1));
    }

    private ResponseEntity<ShortenUrlResponse> createAlias() {
        var body = Map.of(
                "fullUrl", FULL_URL_1,
                "customAlias", ALIAS_1
        );

        var entity = new HttpEntity<>(body, headers());

        return restTemplate.postForEntity(baseUrl() + "/shorten", entity, ShortenUrlResponse.class);
    }

    @Test
    void shorten_aliasAlreadyTaken_400() {
        // Given
        var entity = createAlias();

        // When
        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl() + "/shorten", entity, String.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void redirect_aliasNotFound_404() {
        redirect_aliasNotFound_404("does-not-exist");
    }

    private void redirect_aliasNotFound_404(String alias) {
        // When
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl() + "/" + alias, HttpMethod.GET, new HttpEntity<>(headers()), Void.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void delete_success() {
        // Given
        createAlias();

        // When
        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseUrl() + "/" + ALIAS_1, HttpMethod.DELETE, new HttpEntity<>(headers()), Void.class);

        // Then
        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.NO_CONTENT));

        // Should not be able to redirect using this now deleted alias
        redirect_aliasNotFound_404(ALIAS_1);
    }

    @Test
    void delete_aliasNotFound_404() {
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl() + "/does-not-exist", HttpMethod.DELETE, new HttpEntity<>(headers()), Void.class);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void list_200() {
        restTemplate.postForEntity(baseUrl() + "/shorten",
                new HttpEntity<>(Map.of("fullUrl", FULL_URL_1, "customAlias", ALIAS_1), headers()),
                ShortenUrlResponse.class);

        restTemplate.postForEntity(baseUrl() + "/shorten",
                new HttpEntity<>(Map.of("fullUrl", FULL_URL_2, "customAlias", ALIAS_2), headers()),
                ShortenUrlResponse.class);

        ResponseEntity<UrlControllerImpl.ShortenUrlListResponse[]> response =
                restTemplate.exchange(baseUrl() + "/urls", HttpMethod.GET, new HttpEntity<>(headers()), UrlControllerImpl.ShortenUrlListResponse[].class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().length, is(2));
    }
}
