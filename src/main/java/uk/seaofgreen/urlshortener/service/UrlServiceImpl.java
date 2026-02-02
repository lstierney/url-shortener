package uk.seaofgreen.urlshortener.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.seaofgreen.urlshortener.entity.Url;
import uk.seaofgreen.urlshortener.repository.UrlRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_ALIAS_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

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
            // saveAndFlush to force the DB check immediately within the try-catch block
            return urlRepository.saveAndFlush(new Url(fullUrl, alias));
        } catch (DataIntegrityViolationException e) {
            if (isCustom) {
                // We stop here because the User requested a specific custom alias
                throw new IllegalArgumentException("Alias '" + alias + "' is already taken");
            }

            if (attempt >= maxRetries) {
                throw new RuntimeException("Could not generate unique alias after multiple attempts");
            }
            // Self-healing: try again with a fresh random alias
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
