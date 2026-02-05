export const validateUrlForm = (fullUrl: string, customAlias: string): string | null => {
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
