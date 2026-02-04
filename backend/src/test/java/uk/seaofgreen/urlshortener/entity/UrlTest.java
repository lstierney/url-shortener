package uk.seaofgreen.urlshortener.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UrlTest {
    @Test
    void equalsAndHashCode_equals() {
        Url url1 = new Url("abc", "https://example.com");
        Url url2 = new Url("abc", "https://example.com");

        assertThat(url1, is(url2));
        assertThat(url1.hashCode(), is(url2.hashCode()));
    }

    @Test
    void equalsAndHashCode_notEquals_differentAlias() {
        Url url1 = new Url("xyz", "https://example.com");
        Url url2 = new Url("abc", "https://example.com");

        assertThat(url1, is(not(url2)));
        assertThat(url1.hashCode(), is(not(url2.hashCode())));
    }

    @Test
    void equalsAndHashCode_notEquals_differentFullUrl() {
        Url url1 = new Url("abc", "https://test.com");
        Url url2 = new Url("abc", "https://example.com");

        assertThat(url1, is(not(url2)));
        assertThat(url1.hashCode(), is(not(url2.hashCode())));
    }
}
