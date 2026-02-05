import { render, screen, fireEvent } from "@testing-library/react";
import { describe, test, expect, vi } from "vitest";
import ManageUrls from "./ManageUrls";

describe("ManageUrls", () => {
    test("shows 'None found' when the list is empty", () => {
        render(<ManageUrls urls={[]} onDelete={() => {}} />);

        expect(screen.getByText("None found")).toBeInTheDocument();
    });

    test("renders a list of URLs", () => {
        const urls = [
            {
                alias: "abc",
                fullUrl: "https://example.com/long-url",
                shortUrl: "https://sho.rt/abc"
            },
            {
                alias: "xyz",
                fullUrl: "https://another.com/page",
                shortUrl: "https://sho.rt/xyz"
            }
        ];

        render(<ManageUrls urls={urls} onDelete={() => {}} />);

        // Short URLs as links
        expect(screen.getByText("https://sho.rt/abc")).toBeInTheDocument();
        expect(screen.getByText("https://sho.rt/xyz")).toBeInTheDocument();

        // Full URLs as text
        expect(screen.getByText("https://example.com/long-url")).toBeInTheDocument();
        expect(screen.getByText("https://another.com/page")).toBeInTheDocument();
    });

    test("calls onDelete with the correct alias", () => {
        const urls = [
            {
                alias: "abc",
                fullUrl: "https://example.com/long-url",
                shortUrl: "https://sho.rt/abc"
            }
        ];

        const onDelete = vi.fn();

        render(<ManageUrls urls={urls} onDelete={onDelete} />);

        const deleteButton = screen.getByRole("button", { name: "Delete" });
        fireEvent.click(deleteButton);

        expect(onDelete).toHaveBeenCalledTimes(1);
        expect(onDelete).toHaveBeenCalledWith("abc");
    });
});
