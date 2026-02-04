package uk.seaofgreen.urlshortener.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import uk.seaofgreen.urlshortener.controller.UrlControllerImpl;
import uk.seaofgreen.urlshortener.controller.UrlControllerImpl.ShortenUrlResponse;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.seaofgreen.urlshortener.testsupport.TestDataFactory.*;

/**
 * Full end‑to‑end integration tests using TestRestTemplate.
 *
 * TestRestTemplate is used here because it exercises the application through
 * a real HTTP server, giving us confidence that routing, serialization,
 * validation, filters, exception handling and the controller layer all behave
 * exactly as they do in production. This makes it ideal for testing the
 * overall API contract rather than just controller logic.
 *
 * Redirect behaviour is tested separately with MockMvc, as TestRestTemplate
 * automatically follows redirects and does not expose the raw 3xx response.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    void shorten_success() {
        var body = Map.of(
                "fullUrl", FULL_URL_1,
                "customAlias", ALIAS_1
        );

        var entity = new HttpEntity<>(body, headers());

        ResponseEntity<ShortenUrlResponse> response =
                restTemplate.postForEntity(baseUrl() + "/shorten", entity, ShortenUrlResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().shortUrl(), is("https://localhost:" + port + "/" + ALIAS_1));
    }

    @Test
    void shorten_aliasAlreadyTaken() {
        var body = Map.of(
                "fullUrl", FULL_URL_1,
                "customAlias", ALIAS_1
        );

        var entity = new HttpEntity<>(body, headers());

        // First call succeeds
        restTemplate.postForEntity(baseUrl() + "/shorten", entity, ShortenUrlResponse.class);

        // Second call fails
        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl() + "/shorten", entity, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void redirect_aliasNotFound() {
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl() + "/does-not-exist", HttpMethod.GET, new HttpEntity<>(headers()), Void.class);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void delete_success() {
        var body = Map.of(
                "fullUrl", FULL_URL_1,
                "customAlias", ALIAS_1
        );
        restTemplate.postForEntity(baseUrl() + "/shorten", new HttpEntity<>(body, headers()), ShortenUrlResponse.class);

        ResponseEntity<Void> deleteResponse =
                restTemplate.exchange(baseUrl() + "/" + ALIAS_1, HttpMethod.DELETE, new HttpEntity<>(headers()), Void.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.NO_CONTENT));

        ResponseEntity<Void> redirectResponse =
                restTemplate.exchange(baseUrl() + "/" + ALIAS_1, HttpMethod.GET, new HttpEntity<>(headers()), Void.class);

        assertThat(redirectResponse.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void delete_aliasNotFound() {
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl() + "/does-not-exist", HttpMethod.DELETE, new HttpEntity<>(headers()), Void.class);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void list() {
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
