import { useState } from "react";
import styles from "./ShortenUrl.module.css";
import type { UrlEntry } from "../../api/types";
import { useShortenUrl } from "../../hooks/useShortenUrl";

type ShortenUrlProps = {
    onCreated?: (url: UrlEntry) => void;
};

const ShortenUrl = ({ onCreated }: ShortenUrlProps) => {
    const [fullUrl, setFullUrl] = useState("");
    const [customAlias, setCustomAlias] = useState("");

    const {
        loading,
        shortUrl,
        error,
        validationError,
        handleSubmit,
    } = useShortenUrl({ fullUrl, customAlias, onCreated });

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
                    Short URL Created: <a href={shortUrl} target="_blank" rel="noopener noreferrer">{shortUrl}</a>
                </div>
            )}
        </form>
    );
};

export default ShortenUrl;
