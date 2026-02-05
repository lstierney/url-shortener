import { useState } from "react";
import { shortenUrl } from "../api/api";
import type { UrlEntry } from "../api/types";
import { validateUrlForm } from "../utils/validateUrlForm";

type UseShortenUrlProps = {
    fullUrl: string;
    customAlias: string;
    onCreated?: (url: UrlEntry) => void;
};

export const useShortenUrl = ({
                                  fullUrl,
                                  customAlias,
                                  onCreated,
                              }: UseShortenUrlProps) => {
    const [loading, setLoading] = useState(false);
    const [shortUrl, setShortUrl] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [validationError, setValidationError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setValidationError(null);
        setError(null);
        setShortUrl(null);

        const validation = validateUrlForm(fullUrl, customAlias);
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

    return {
        loading,
        shortUrl,
        error,
        validationError,
        handleSubmit,
    };
};
