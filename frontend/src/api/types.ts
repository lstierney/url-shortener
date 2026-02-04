export type UrlEntry = {
    alias: string;
    fullUrl: string;
    shortUrl: string;
};

export type ShortenRequest = {
    fullUrl: string;
    customAlias?: string;
};

export type ShortenResponse = {
    shortUrl: string;
};
