package uk.seaofgreen.urlshortener.controller.web;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.seaofgreen.urlshortener.controller.UrlController;
import uk.seaofgreen.urlshortener.service.UrlService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static uk.seaofgreen.urlshortener.testsupport.TestDataFactory.*;

@WebMvcTest(UrlController.class)
class UrlControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void list() throws Exception {
        // Given
        given(urlService.getAllUrls()).willReturn(List.of(url1(), url2()));

        // When + Then
        mockMvc.perform(get("/urls")
                        .header("Host", HOST)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))

                // First element
                .andExpect(jsonPath("$[0].alias", is(ALIAS_1)))
                .andExpect(jsonPath("$[0].fullUrl", is(FULL_URL_1)))
                .andExpect(jsonPath("$[0].shortUrl", is(SHORT_URL_1)))

                // Second element
                .andExpect(jsonPath("$[1].alias", is(ALIAS_2)))
                .andExpect(jsonPath("$[1].fullUrl", is(FULL_URL_2)))
                .andExpect(jsonPath("$[1].shortUrl", is(SHORT_URL_2)));
    }

    @Test
    void delete_recordFound() throws Exception {
        // Given
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.of(url1()));

        // When + Then
        mockMvc.perform(delete("/" + ALIAS_1))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_recordNotFound() throws Exception {
        // Given
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.empty());

        // When + Then
        mockMvc.perform(delete("/" + ALIAS_1))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_aliasNotFound() throws Exception {
        // Given
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.empty());

        // When + Then
        mockMvc.perform(get("/" + ALIAS_1))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_aliasFound() throws Exception {
        // Given
        var url = url1();
        given(urlService.getUrl(ALIAS_1)).willReturn(Optional.of(url));

        // When + Then
        mockMvc.perform(get("/" + ALIAS_1))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", url.getFullUrl()));
    }

    @Test
    void shorten_success() throws Exception {
        // Given
        var requestJson = """
            {"fullUrl":"%s","customAlias":"%s"}
        """.formatted(FULL_URL_1, ALIAS_1);


        given(urlService.createUrl(FULL_URL_1, Optional.of(ALIAS_1)))
                .willReturn(url1());

        // When + Then
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Host", HOST)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl", is(SHORT_URL_1)));
    }

    @Test
    void shorten_aliasTaken() throws Exception {
        // Given
        var requestJson = """
            {"fullUrl":"%s","customAlias":"%s"}
        """.formatted(FULL_URL_1, ALIAS_1);

        given(urlService.createUrl(FULL_URL_1, Optional.of(ALIAS_1)))
                .willThrow(new IllegalArgumentException("alias taken"));

        // When + Then
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Host", HOST)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shorten_fullUrlMissing() throws Exception {
        var requestJson = """
            {"customAlias":"%s"}
        """.formatted(ALIAS_1);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Host", HOST)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shorten_nullCustomAlias() throws Exception {
        // Given
        var requestJson = """
        {"fullUrl":"%s"}
        """.formatted(FULL_URL_1);

        var createdUrl = url(FULL_URL_1, ALIAS_1);
        given(urlService.createUrl(FULL_URL_1, Optional.empty()))
                .willReturn(createdUrl);

        // When + Then
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Host", HOST)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl", is(SHORT_URL_1)));
    }


}
