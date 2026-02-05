package com.tpximpact.urlshortener.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import com.tpximpact.urlshortener.entity.Url;
import com.tpximpact.urlshortener.repository.UrlRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class UrlServiceImplTest {
    private static final String TEST_COM = "https://test.com";

    @Mock(strictness = Mock.Strictness.STRICT_STUBS)
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    @AfterEach
    void after() {
        // Fails if there are any interactions not verified
        Mockito.verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void createUrl_withCustomAlias_happyPath() {
        // Given
        String customAlias = "test";
        Url expectedUrl = new Url(TEST_COM, customAlias);

        given(urlRepository.saveAndFlush(expectedUrl)).willReturn(expectedUrl);

        // When
        Url actualUrl = urlService.createUrl(TEST_COM, Optional.of(customAlias));

        // Then
        assertThat(actualUrl.getAlias(), is(equalTo(customAlias)));
        assertThat(actualUrl.getFullUrl(), is(equalTo(TEST_COM)));

        then(urlRepository).should().saveAndFlush(expectedUrl);
    }

    @Test
    void createUrl_withCustomAlias_AlreadyTaken_ThrowsException() {
        // Given
        String customAlias = "taken";
        given(urlRepository.saveAndFlush(new Url(TEST_COM, customAlias)))
                .willThrow(new DataIntegrityViolationException("Duplicate entry"));

        // When
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                urlService.createUrl(TEST_COM, Optional.of(customAlias))
        );

        // Then
        assertThat(ex.getMessage(), containsString("already taken"));

        then(urlRepository).should().saveAndFlush(new Url(TEST_COM, customAlias));
    }

    @Test
    void createUrl_withRandomAlias_happyPath() {
        // Given
        given(urlRepository.saveAndFlush(any(Url.class))).willReturn(new Url(TEST_COM, "random"));

        // When
        Url result = urlService.createUrl(TEST_COM, Optional.empty());

        // Then
        assertThat(result.getAlias(), is(notNullValue()));
        assertThat(result.getAlias().length(), is(6));
        then(urlRepository).should().saveAndFlush(any(Url.class));
    }

    @Test
    void createUrl_withRandomAlias_RetriesOnCollision_ThenSucceeds() {
        // Given
        Url third = new Url(TEST_COM, "third-time-lucky");

        given(urlRepository.saveAndFlush(any(Url.class)))
                .willThrow(new DataIntegrityViolationException("Collision 1"))
                .willThrow(new DataIntegrityViolationException("Collision 2"))
                .willReturn(third);

        // When
        Url result = urlService.createUrl(TEST_COM, Optional.empty());

        // Then
        assertThat(result.getAlias(), is("third-time-lucky"));

        then(urlRepository).should(times(3)).saveAndFlush(any(Url.class));
    }

    @Test
    void createUrl_withRandomAlias_ExceedsMaxRetries_ThrowsException() {
        // Given
        given(urlRepository.saveAndFlush(any(Url.class)))
                .willThrow(new DataIntegrityViolationException("Forever colliding"));

        // When
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                urlService.createUrl(TEST_COM, Optional.empty())
        );

        // Then
        assertThat(ex.getMessage(), containsString("multiple attempts"));

        then(urlRepository).should(times(6)).saveAndFlush(any(Url.class));
    }

    @Test
    void getUrl() {
        // Given
        String alias = "abc";
        Url url = new Url(TEST_COM, alias);
        given(urlRepository.findByAlias(alias)).willReturn(Optional.of(url));

        // When
        Optional<Url> result = urlService.getUrl(alias);

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getAlias(), is(alias));

        then(urlRepository).should().findByAlias(alias);
    }

    @Test
    void deleteUrl_recordFound() {
        // Given
        String alias = "alias";
        Url url = new Url(TEST_COM, alias);
        given(urlRepository.findByAlias(alias)).willReturn(Optional.of(url));

        // When
        urlService.deleteUrl(alias);

        // Then
        then(urlRepository).should().findByAlias(alias);
        then(urlRepository).should().delete(eq(url));
    }

    @Test
    void deleteUrl_recordNotFound() {
        // Given
        String alias = "alias";
        given(urlRepository.findByAlias(alias)).willReturn(Optional.empty());

        // When
        urlService.deleteUrl(alias);

        // Then
        then(urlRepository).should().findByAlias(alias);
        then(urlRepository).should(never()).delete(any());
    }

    @Test
    void getAllUrls() {
        // When
        urlService.getAllUrls();

        // Then
        then(urlRepository).should().findAll();
    }
}
