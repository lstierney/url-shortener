import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import { act } from "react";
import UrlManager from "./UrlManager";

vi.mock("../../api/api", () => ({
    listUrls: vi.fn(),
    deleteUrl: vi.fn()
}));

vi.mock("../ShortenUrl/ShortenUrl", () => ({
    default: ({ onCreated }: any) => (
        <button
            onClick={() =>
                onCreated({
                    alias: "new",
                    fullUrl: "https://new.com",
                    shortUrl: "https://sho.rt/new"
                })
            }
        >
            MockCreate
        </button>
    )
}));

vi.mock("../ManageUrls/ManageUrls", () => ({
    default: ({ urls, onDelete }: any) => (
        <div>
            {urls.map((u: any) => (
                <div key={u.alias}>
                    <span>{u.shortUrl}</span>
                    <button onClick={() => onDelete(u.alias)}>Delete</button>
                </div>
            ))}
        </div>
    )
}));

import { listUrls, deleteUrl } from "../../api/api";

describe("UrlManager", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    test("loads and displays URLs", async () => {
        (listUrls as any).mockResolvedValue([
            {
                alias: "abc",
                fullUrl: "https://example.com",
                shortUrl: "https://sho.rt/abc"
            }
        ]);

        await act(async () => {
            render(<UrlManager />);
        });

        await waitFor(() =>
            expect(screen.getByText("https://sho.rt/abc")).toBeInTheDocument()
        );
    });

    test("adds a new URL when ShortenUrl triggers onCreated", async () => {
        (listUrls as any).mockResolvedValue([]);

        await act(async () => {
            render(<UrlManager />);
        });

        await waitFor(() =>
            expect(screen.queryByText("Loading…")).not.toBeInTheDocument()
        );

        await act(async () => {
            fireEvent.click(screen.getByText("MockCreate"));
        });

        await waitFor(() =>
            expect(screen.getByText("https://sho.rt/new")).toBeInTheDocument()
        );
    });

    test("deletes a URL when ManageUrls triggers onDelete", async () => {
        (listUrls as any).mockResolvedValue([
            {
                alias: "abc",
                fullUrl: "https://example.com",
                shortUrl: "https://sho.rt/abc"
            }
        ]);

        (deleteUrl as any).mockResolvedValue(undefined);

        await act(async () => {
            render(<UrlManager />);
        });

        await waitFor(() =>
            expect(screen.getByText("https://sho.rt/abc")).toBeInTheDocument()
        );

        await act(async () => {
            fireEvent.click(screen.getByText("Delete"));
        });

        await waitFor(() =>
            expect(screen.queryByText("https://sho.rt/abc")).toBeNull()
        );

        expect(deleteUrl).toHaveBeenCalledWith("abc");
    });
});
