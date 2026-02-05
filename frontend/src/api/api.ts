import { API_BASE } from "../../api.ts";
import type {UrlEntry, ShortenRequest, ShortenResponse} from "./types";

export async function listUrls(): Promise<UrlEntry[]> {
    const res = await fetch(`${API_BASE}/urls`);
    if (!res.ok) throw new Error("Failed to load URLs");
    return res.json();
}

export async function shortenUrl(
    payload: ShortenRequest
): Promise<ShortenResponse> {
    const res = await fetch(`${API_BASE}/shorten`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
    });

    if (res.status === 400) {
        throw new Error("Invalid input or alias already taken");
    }

    if (res.status !== 201) {
        throw new Error("Unexpected error");
    }

    return res.json();
}

export async function deleteUrl(alias: string): Promise<void> {
    const res = await fetch(`${API_BASE}/${alias}`, { method: "DELETE" });

    if (res.status === 404) {
        throw new Error("Requested URL not found");
    }

    if (res.status !== 204) {
        throw new Error("Unexpected error");
    }
}
