package com.tpximpact.urlshortener.testsupport;

import com.tpximpact.urlshortener.controller.UrlControllerImpl.ShortenUrlListResponse;
import com.tpximpact.urlshortener.controller.UrlControllerImpl.ShortenUrlRequest;
import com.tpximpact.urlshortener.controller.UrlControllerImpl.ShortenUrlResponse;
import com.tpximpact.urlshortener.entity.Url;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static final String HOST = "hostname";
    public static final String ALIAS_1 = "example";
    public static final String ALIAS_2 = "google";
    public static final String FULL_URL_1 = "https://example.com";
    public static final String FULL_URL_2 = "https://google.com";
    public static final String SHORT_URL_1 = "https://hostname/example";
    public static final String SHORT_URL_2 = "https://hostname/google";

    public static Url url(String fullUrl, String alias) {
        return new Url(fullUrl, alias);
    }

    public static Url url1() {
        return url(FULL_URL_1, ALIAS_1);
    }

    public static Url url2() {
        return url(FULL_URL_2, ALIAS_2);
    }

    public static ShortenUrlResponse response(String shortUrl) {
        return new ShortenUrlResponse(shortUrl);
    }

    public static ShortenUrlResponse response1() {
        return response(SHORT_URL_1);
    }

    public static ShortenUrlResponse response2() {
        return response(SHORT_URL_2);
    }

    public static ShortenUrlRequest request(String fullUrl, String customAlias) {
        return new ShortenUrlRequest(fullUrl, customAlias);
    }

    public static ShortenUrlRequest request1() {
        return request(FULL_URL_1, ALIAS_1);
    }

    public static ShortenUrlListResponse listResponse(String alias, String fullUrl, String shortUrl) {
        return new ShortenUrlListResponse(alias, fullUrl, shortUrl);
    }

    public static ShortenUrlListResponse listResponse1() {
        return listResponse(ALIAS_1, FULL_URL_1, SHORT_URL_1);
    }

    public static ShortenUrlListResponse listResponse2() {
        return listResponse(ALIAS_2, FULL_URL_2, SHORT_URL_2);
    }
}
