import "@testing-library/jest-dom";
import { afterAll, afterEach, beforeAll } from "vitest";
import { cleanup } from "@testing-library/react";
import { mswServer } from "./msw/msw-server";

beforeAll(() => mswServer.listen({ onUnhandledRequest: "error" }));

afterEach(() => {
  vi.clearAllMocks();
  mswServer.resetHandlers();
  cleanup();
});

afterAll(() => mswServer.close());
