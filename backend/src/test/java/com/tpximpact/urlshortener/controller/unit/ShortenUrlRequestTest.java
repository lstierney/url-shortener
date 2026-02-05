package com.tpximpact.urlshortener.controller.unit;

import com.tpximpact.urlshortener.controller.UrlControllerImpl.ShortenUrlRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.tpximpact.urlshortener.testsupport.TestDataFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ShortenUrlRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest() {
        var dto = new ShortenUrlRequest(
                FULL_URL_1,
                ALIAS_1
        );

        Set<ConstraintViolation<ShortenUrlRequest>> violations =
                validator.validate(dto);

        assertThat(violations, is(empty()));
    }

    @Test
    void missingUrl() {
        var dto = new ShortenUrlRequest(
                "",
                null
        );

        Set<ConstraintViolation<ShortenUrlRequest>> violations =
                validator.validate(dto);

        assertThat(violations, hasSize(1));
        assertThat(
                violations.iterator().next().getMessage(),
                containsString("must not be blank")
        );
    }

    @Test
    void invalidUrl() {
        var dto = new ShortenUrlRequest(
                "not-a-url",
                "alias"
        );

        Set<ConstraintViolation<ShortenUrlRequest>> violations =
                validator.validate(dto);

        assertThat(violations, hasSize(1));
        assertThat(
                violations.iterator().next().getMessage(),
                containsString("valid URL")
        );
    }

    @Test
    void invalidAlias() {
        var dto = new ShortenUrlRequest(
                FULL_URL_2,
                "bad alias!" // spaces + punctuation
        );

        Set<ConstraintViolation<ShortenUrlRequest>> violations =
                validator.validate(dto);

        assertThat(violations, hasSize(1));
        assertThat(
                violations.iterator().next().getMessage(),
                containsString("letters, numbers, hyphens")
        );
    }

    @Test
    void aliasTooLong() {
        var dto = new ShortenUrlRequest(
                FULL_URL_1,
                "this_alias_is_way_too_long_12345"
        );

        Set<ConstraintViolation<ShortenUrlRequest>> violations =
                validator.validate(dto);

        assertThat(violations, hasSize(1));
        assertThat(
                violations.iterator().next().getMessage(),
                containsString("20 characters")
        );
    }
}

