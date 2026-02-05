package com.tpximpact.urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tpximpact.urlshortener.entity.Url;
import com.tpximpact.urlshortener.repository.UrlRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_ALIAS_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();
    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    @Transactional
    public Url createUrl(String fullUrl, Optional<String> customAlias) {
        String alias = customAlias.orElseGet(this::generateRandomAlias);
        return saveWithRetry(fullUrl, alias, customAlias.isPresent(), 0);
    }

    private String generateRandomAlias() {
        StringBuilder sb = new StringBuilder(RANDOM_ALIAS_LENGTH);
        for (int i = 0; i < RANDOM_ALIAS_LENGTH; i++) {
            sb.append(ALPHANUM.charAt(random.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }

    private Url saveWithRetry(String fullUrl, String alias, boolean isCustom, int attempt) {
        int maxRetries = 5;

        try {
            log.debug("Attempting to save URL '{}' with alias '{}' (attempt {})", fullUrl, alias, attempt);
            Url saved = urlRepository.saveAndFlush(new Url(fullUrl, alias));
            log.info("Successfully created short URL with alias '{}'", alias);
            return saved;

        } catch (DataIntegrityViolationException e) {
            if (isCustom) {
                log.warn("Custom alias '{}' is already taken — rejecting request", alias);
                throw new IllegalArgumentException("Alias '" + alias + "' is already taken");
            }

            if (attempt >= maxRetries) {
                log.error("Failed to generate a unique alias after {} attempts", maxRetries);
                throw new RuntimeException("Could not generate unique alias after multiple attempts");
            }

            log.debug("Alias '{}' collided (attempt {}). Generating a new alias…", alias, attempt);
            return saveWithRetry(fullUrl, generateRandomAlias(), false, attempt + 1);
        }
    }

    @Override
    public Optional<Url> getUrl(String alias) {
        return urlRepository.findByAlias(alias);
    }

    @Override
    public void deleteUrl(String alias) {
        urlRepository.findByAlias(alias).ifPresent(urlRepository::delete);
    }

    @Override
    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }
}
