import { render, screen } from "@testing-library/react";
import { describe, test, expect } from "vitest";
import Page from "./Page";

describe("Page", () => {
    test("renders children", () => {
        render(
            <Page>
                <p>Hello world</p>
            </Page>
        );

        expect(screen.getByText("Hello world")).toBeInTheDocument();
    });

    test("renders title when provided", () => {
        render(
            <Page title="My Page Title">
                <div>Content</div>
            </Page>
        );

        expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent("My Page Title");
    });

    test("does not render title when not provided", () => {
        render(
            <Page>
                <div>Content</div>
            </Page>
        );

        expect(screen.queryByRole("heading")).toBeNull();
    });
});
