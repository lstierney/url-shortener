package uk.seaofgreen.urlshortener.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import uk.seaofgreen.urlshortener.entity.Url;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@ActiveProfiles("test")
class UrlRepositoryTest {
    private static final String FULL_URL = "https://google.com";
    private static final String ALIAS = "google";

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void findByAlias() {
        // Given
        Url url = new Url(FULL_URL, ALIAS);
        urlRepository.save(url);

        // When
        Optional<Url> found = urlRepository.findByAlias(ALIAS);

        // Then
        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getFullUrl(), is(FULL_URL));
    }
}
