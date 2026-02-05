import { render, screen } from "@testing-library/react";
import { describe, test, expect } from "vitest";

import Card from "./Card";

describe("Card", () => {
    test("renders children", () => {
        render(
            <Card>
                <p>Hello world</p>
            </Card>
        );

        expect(screen.getByText("Hello world")).toBeInTheDocument();
    });

    test("renders title when provided", () => {
        render(
            <Card title="My Title">
                <div>Content</div>
            </Card>
        );

        expect(screen.getByRole("heading", { level: 2 })).toHaveTextContent("My Title");
    });

    test("does not render title when not provided", () => {
        render(
            <Card>
                <div>Content</div>
            </Card>
        );

        expect(screen.queryByRole("heading")).toBeNull();
    });
});
