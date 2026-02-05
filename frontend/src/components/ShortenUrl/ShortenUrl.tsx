import { useState } from "react";
import styles from "./ShortenUrl.module.css";
import { shortenUrl } from "../../api/api";
import type { UrlEntry } from "../../api/types";

type ShortenUrlProps = {
    onCreated?: (url: UrlEntry) => void;
};

const ShortenUrl = ({ onCreated }: ShortenUrlProps) => {
    const [fullUrl, setFullUrl] = useState("");
    const [customAlias, setCustomAlias] = useState("");

    const [loading, setLoading] = useState(false);
    const [shortUrl, setShortUrl] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [validationError, setValidationError] = useState<string | null>(null);

    const validate = () => {
        try {
            const parsed = new URL(fullUrl);
            if (!parsed.protocol.startsWith("http")) {
                return "URL must start with http:// or https://";
            }
        } catch {
            return "Please enter a valid URL";
        }

        if (customAlias.trim().length > 0) {
            const aliasRegex = /^[a-zA-Z0-9-_]+$/;
            if (!aliasRegex.test(customAlias)) {
                return "Alias may only contain letters, numbers, hyphens, and underscores";
            }
            if (customAlias.length > 20) {
                return "Alias must be 20 characters or fewer";
            }
        }

        return null;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setValidationError(null);
        setError(null);
        setShortUrl(null);

        const validation = validate();
        if (validation) {
            setValidationError(validation);
            return;
        }

        setLoading(true);

        try {
            const data = await shortenUrl({
                fullUrl,
                customAlias: customAlias || undefined,
            });

            setShortUrl(data.shortUrl);

            // Build full UrlEntry for the table
            const alias =
                customAlias.trim() ||
                new URL(data.shortUrl).pathname.replace(/^\//, "");

            const entry: UrlEntry = {
                alias,
                fullUrl,
                shortUrl: data.shortUrl,
            };

            onCreated?.(entry);
        } catch (err: any) {
            setError(err.message || "Unexpected error");
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <label className={styles.label}>
                Full URL (required)
                <input
                    type="text"
                    className={styles.input}
                    value={fullUrl}
                    onChange={(e) => setFullUrl(e.target.value)}
                    required
                />
            </label>

            <label className={styles.label}>
                Custom Alias (optional)
                <input
                    type="text"
                    className={styles.input}
                    value={customAlias}
                    onChange={(e) => setCustomAlias(e.target.value)}
                />
            </label>

            <button type="submit" className={styles.button} disabled={loading}>
                {loading ? "Shortening..." : "Shorten URL"}
            </button>

            {validationError && <div className={styles.error}>{validationError}</div>}
            {error && <div className={styles.error}>{error}</div>}
            {shortUrl && (
                <div className={styles.success}>
                    Short URL Created: <a href={shortUrl}>{shortUrl}</a>
                </div>
            )}
        </form>
    );
};

export default ShortenUrl;
