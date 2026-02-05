package com.tpximpact.urlshortener.service;

import com.tpximpact.urlshortener.entity.Url;

import java.util.List;
import java.util.Optional;

public interface UrlService {

    /**
     * Create a shortened URL.
     *
     * @param fullUrl the original URL
     * @param customAlias optional custom alias
     * @return the saved Url entity
     */
    Url createUrl(String fullUrl, Optional<String> customAlias);

    /**
     * Retrieve a Url by its alias.
     *
     * @param alias the alias to look up
     * @return Optional containing Url if found
     */
    Optional<Url> getUrl(String alias);

    /**
     * Delete a Url by alias.
     *
     * @param alias the alias to delete
     */
    void deleteUrl(String alias);

    /**
     * Get all shortened URLs.
     *
     * @return list of all Url entities
     */
    List<Url> getAllUrls();
}

