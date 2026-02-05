package com.tpximpact.urlshortener.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.tpximpact.urlshortener.service.UrlService;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.tpximpact.urlshortener.testsupport.TestDataFactory.ALIAS_1;
import static com.tpximpact.urlshortener.testsupport.TestDataFactory.FULL_URL_1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Redirect behaviour is tested separately with MockMvc because
 * TestRestTemplate automatically follows redirects and cannot
 * easily be configured to return the raw 302 response. MockMvc
 * exposes the controller’s actual redirect status and headers,
 * making it the appropriate tool for this specific case.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RedirectMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UrlService urlService;

    @Test
    void redirect() throws Exception {
        urlService.createUrl(FULL_URL_1, Optional.of(ALIAS_1));

        mockMvc.perform(get("/" + ALIAS_1))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", FULL_URL_1));
    }
}

