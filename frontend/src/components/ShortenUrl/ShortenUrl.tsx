import { useState } from "react";
import styles from "./ShortenUrl.module.css";
import { API_BASE } from "../../../api.ts";
import type { UrlEntry } from "../ManageUrls/ManageUrls.tsx";

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
            if (customAlias.length > 50) {
                return "Alias must be 50 characters or fewer";
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
            const response = await fetch(`${API_BASE}/shorten`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    fullUrl,
                    customAlias: customAlias || undefined,
                }),
            });

            if (response.status === 201) {
                const data = await response.json();
                setShortUrl(data.shortUrl);

                // Build a full UrlEntry for the table
                const aliasFromResponse =
                    customAlias.trim() ||
                    new URL(data.shortUrl).pathname.replace(/^\//, "");

                const entry: UrlEntry = {
                    alias: aliasFromResponse,
                    fullUrl,
                    shortUrl: data.shortUrl,
                };

                onCreated?.(entry);
            } else if (response.status === 400) {
                setError("Invalid input or alias already taken");
            } else {
                setError("Unexpected error from server");
            }
        } catch {
            setError("Network error — could not reach server");
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
                    Short URL: <a href={shortUrl}>{shortUrl}</a>
                </div>
            )}
        </form>
    );
};

export default ShortenUrl;
