import "@testing-library/jest-dom";
import { afterAll, afterEach, beforeAll } from "vitest";
import { cleanup } from "@testing-library/react";
import { mswServer } from "./msw/msw-server";
import { forwardRef } from "react";
import type { Ref } from "react";

process.env.TZ = "Europe/Warsaw";

beforeAll(() => mswServer.listen({ onUnhandledRequest: "error" }));

afterEach(() => {
  vi.clearAllMocks();
  mswServer.resetHandlers();
  cleanup();
});

afterAll(() => mswServer.close());

vi.mock(import("xlsx"), async (importOriginal) => {
  const actual = await importOriginal();

  return {
    ...actual,
    read: vi.fn(),
    utils: {
      ...actual.utils,
      sheet_to_json: vi.fn(),
    },
  };
});

vi.mock(import("recharts"), async (importActual) => {
  const mockRecharts = await importActual();
  return {
    ...mockRecharts,
    ResponsiveContainer: forwardRef(({ children }, ref) => (
      <div
        ref={ref as Ref<HTMLDivElement>}
        style={{ width: 800, height: 800 }}
        data-testid="responsive-container-mock"
      >
        {children}
      </div>
    )),
  };
});
