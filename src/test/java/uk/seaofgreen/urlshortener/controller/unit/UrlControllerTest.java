package uk.seaofgreen.urlshortener.controller.unit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.seaofgreen.urlshortener.controller.UrlControllerImpl;
import uk.seaofgreen.urlshortener.controller.UrlControllerImpl.ShortenUrlRequest;
import uk.seaofgreen.urlshortener.service.UrlService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static uk.seaofgreen.urlshortener.testsupport.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlControllerImpl urlController;

    @AfterEach
    void after() {
        verifyNoMoreInteractions(urlService);
    }

    @Test
    void delete_recordFound() {
        // Given
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.of(url1()));

        // When
        var response = urlController.delete(ALIAS_1);

        // Then
        then(urlService).should().getUrl(ALIAS_1);
        then(urlService).should().deleteUrl(ALIAS_1);

        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    void delete_recordNotFound() {
        // Given
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.empty());

        // When
        var response = urlController.delete(ALIAS_1);

        // Then
        then(urlService).should().getUrl(ALIAS_1);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }


    @Test
    void list() {
        // Given
        given(urlService.getAllUrls()).willReturn(List.of(url1(), url2()));

        // When
        var responses = urlController.list(HOST);

        // Then
        then(urlService).should().getAllUrls();

        assertThat(responses, contains(
                listResponse1(),
                listResponse2()
            )
        );
        // Dont test HTTP Code always wrapped in a 200 by Spring
    }

    @Test
    void redirect_aliasNotFound() {
        // Given
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.empty());

        // When
        var response = urlController.redirect(ALIAS_1);

        // Then
        then(urlService).should().getUrl(ALIAS_1);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void redirect_aliasFound() {
        // Given
        var url = url1();
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.of(url1()));

        // When
        var response = urlController.redirect(ALIAS_1);

        // Then
        then(urlService).should().getUrl(ALIAS_1);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getFirst("Location"), is(url.getFullUrl()));
    }

    @Test
    void shorten_success() {
        // Given
        var request = new ShortenUrlRequest(FULL_URL_1, ALIAS_1);
        var createdUrl = url(FULL_URL_1, ALIAS_1);

        given(urlService.createUrl(request.fullUrl(), Optional.of(request.customAlias())))
                .willReturn(createdUrl);

        // When
        var response = urlController.shorten(request, HOST);

        // Then
        then(urlService).should().createUrl(request.fullUrl(), Optional.of(ALIAS_1));

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody().shortUrl(), is("https://" + HOST + "/" + ALIAS_1));
    }

    @Test
    void shorten_aliasAlreadyInUse() {
        // Given
        var request = new ShortenUrlRequest(FULL_URL_1, ALIAS_1);

        given(urlService.createUrl(anyString(), any()))
                .willThrow(new IllegalArgumentException("alias taken"));

        // When
        var response = urlController.shorten(request, HOST);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void shorten_customAliasWasNull() {
        // Given
        var request = request(FULL_URL_1, null);
        var createdUrl = url(FULL_URL_1, ALIAS_1);

        given(urlService.createUrl(FULL_URL_1, Optional.empty()))
                .willReturn(createdUrl);

        // When
        var response = urlController.shorten(request, HOST);

        // Then
        then(urlService).should().createUrl(FULL_URL_1, Optional.empty());
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody().shortUrl(), is(SHORT_URL_1));
    }



}
