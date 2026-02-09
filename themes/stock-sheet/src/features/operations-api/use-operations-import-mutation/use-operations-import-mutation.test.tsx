import { renderHook, screen, waitFor } from "@testing-library/react";
import { useOperationsImportMutation } from "./use-operations-import-mutation";
import { TestProviders } from "@/test/test-utils";
import { QueryClient } from "@tanstack/react-query";
import type { MswRequest } from "@/test/types";
import { mswServer } from "@/test/msw/msw-server";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { $apiStockSheet } from "@/apis/stock-sheet/client";

describe("useOperationsImportMutation", () => {
  let importRequestMsw: Array<MswRequest> = [];

  beforeEach(() => {
    importRequestMsw = [];

    mswServer.use(
      $mswStockSheetApi.post(
        "/api/operations/import/{currency}",
        ({ response, request }) => {
          importRequestMsw.push(request);
          return response(200).json({
            added: [
              { id: 100, externalId: "external-id-100" },
              { id: 101, externalId: "external-id-101" },
            ],
            duplicated: [
              { id: 200, externalId: "external-id-200" },
              { id: 201, externalId: "external-id-201" },
            ],
          });
        },
      ),
    );
  });

  test("sends request to correct URL with currency", async () => {
    const { result } = renderHook(() => useOperationsImportMutation(), {
      wrapper: TestProviders,
    });

    result.current.mutate({
      params: { path: { currency: "USD" } },
      body: { operations: [] },
    });

    await waitFor(() => {
      expect(importRequestMsw).toHaveLength(1);
    });
    expect(importRequestMsw[0].url).toContain("/api/operations/import/USD");
  });

  test("shows success toast with both added and duplicated counts", async () => {
    const { result } = renderHook(() => useOperationsImportMutation(), {
      wrapper: TestProviders,
    });

    result.current.mutate({
      params: { path: { currency: "USD" } },
      body: { operations: [] },
    });

    expect(
      await screen.findByRole("region", {
        name: (_, element) => {
          return (
            element.textContent ===
            "Import zakończonyNowe pozycje: 2 | Pominięte duplikaty: 2"
          );
        },
      }),
    ).toBeVisible();
  });

  test("shows success toast without duplicates part when none exist", async () => {
    mswServer.use(
      $mswStockSheetApi.post(
        "/api/operations/import/{currency}",
        ({ response, request }) => {
          importRequestMsw.push(request);
          return response(200).json({
            added: [
              { id: 100, externalId: "external-id-100" },
              { id: 101, externalId: "external-id-101" },
            ],
            duplicated: [],
          });
        },
      ),
    );
    const { result } = renderHook(() => useOperationsImportMutation(), {
      wrapper: TestProviders,
    });

    result.current.mutate({
      params: { path: { currency: "USD" } },
      body: { operations: [] },
    });

    expect(
      await screen.findByRole("region", {
        name: (_, element) => {
          return element.textContent === "Import zakończonyNowe pozycje: 2";
        },
      }),
    ).toBeVisible();
  });

  test("should invalidate portfolio queries on success", async () => {
    const queryClient = new QueryClient();
    vi.spyOn(queryClient, "invalidateQueries");

    const { result } = renderHook(() => useOperationsImportMutation(), {
      wrapper: ({ children }) => (
        <TestProviders queryClient={queryClient}>{children}</TestProviders>
      ),
    });

    result.current.mutate({
      params: { path: { currency: "USD" } },
      body: { operations: [] },
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(queryClient.invalidateQueries).toHaveBeenCalledWith({
      queryKey: $apiStockSheet.queryOptions(
        "get",
        "/api/operations/portfolio/{currency}",
        { params: { path: { currency: "USD" } } },
      ).queryKey,
    });
  });
});
