import { renderHook, screen } from "@testing-library/react";
import { TestProviders } from "@/test/test-utils";
import type { MswRequest } from "@/test/types";
import { mswServer } from "@/test/msw/msw-server";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { useDeletePortfolioMutation } from "./use-delete-portfolio-mutation";
import { QueryClient } from "@tanstack/react-query";
import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { mockErrorResponse } from "@/apis/stock-sheet/mocks/get-error-response.mock";
import { produce } from "immer";
import { PortfolioErrorType } from "@/features/error-response-utils/types";

describe("useDeletePortfolioMutation", () => {
  let deleteRequestMsw: Array<MswRequest> = [];

  const portfolioId = 123123;
  const portfolioName = "portfolio_name_1";

  beforeEach(() => {
    deleteRequestMsw = [];

    mswServer.use(
      $mswStockSheetApi.delete(
        "/api/portfolio/{id}",
        ({ response, request }) => {
          deleteRequestMsw.push(request);
          return response(204).empty();
        },
      ),
    );
  });

  test("calls the delete endpoint with the provided portfolio ID", async () => {
    const { result } = renderHook(() => useDeletePortfolioMutation(), {
      wrapper: TestProviders,
    });

    await result.current.mutateAsync(portfolioId, portfolioName);

    expect(deleteRequestMsw).toHaveLength(1);
    expect(deleteRequestMsw[0].url).toContain(`/api/portfolio/${portfolioId}`);
  });

  test("shows a success notification with the portfolio name after successful deletion", async () => {
    const { result } = renderHook(() => useDeletePortfolioMutation(), {
      wrapper: TestProviders,
    });

    await result.current.mutateAsync(portfolioId, portfolioName);

    expect(
      await screen.findByRole("region", {
        name: (_, element) => {
          return (
            element.textContent ===
            `Portfolio "${portfolioName}" zostało pomyślnie usunięte.`
          );
        },
      }),
    ).toBeVisible();
  });

  test("shows a generic error notification", async () => {
    mswServer.use(
      $mswStockSheetApi.delete(
        "/api/portfolio/{id}",
        ({ response, request }) => {
          deleteRequestMsw.push(request);
          return response(500).json(mockErrorResponse);
        },
      ),
    );
    const { result } = renderHook(() => useDeletePortfolioMutation(), {
      wrapper: TestProviders,
    });

    await result.current.mutateAsync(portfolioId, portfolioName);

    expect(
      await screen.findByRole("region", {
        name: (_, element) => {
          return (
            element.textContent ===
            `Nie udało się usunąć portfolio "${portfolioName}".`
          );
        },
      }),
    ).toBeVisible();
  });

  test(`shows a specific "not found" notification when the portfolio does not exist in the database`, async () => {
    mswServer.use(
      $mswStockSheetApi.delete(
        "/api/portfolio/{id}",
        ({ response, request }) => {
          deleteRequestMsw.push(request);
          return response(500).json(
            produce(mockErrorResponse, (draft) => {
              draft.errors![0].type = PortfolioErrorType.PORTFOLIO_NOT_FOUND;
            }),
          );
        },
      ),
    );
    const { result } = renderHook(() => useDeletePortfolioMutation(), {
      wrapper: TestProviders,
    });

    await result.current.mutateAsync(portfolioId, portfolioName);

    expect(
      await screen.findByRole("region", {
        name: (_, element) => {
          return (
            element.textContent ===
            `Nie możemy znaleźć tego portfolio. Prawdopodobnie zostało już wcześniej usunięte.`
          );
        },
      }),
    ).toBeVisible();
  });

  test("triggers a refresh of the portfolio list by invalidating relevant queries after success", async () => {
    const queryClient = new QueryClient();
    vi.spyOn(queryClient, "invalidateQueries");

    const { result } = renderHook(() => useDeletePortfolioMutation(), {
      wrapper: ({ children }) => (
        <TestProviders queryClient={queryClient}>{children}</TestProviders>
      ),
    });

    await result.current.mutateAsync(portfolioId, portfolioName);

    expect(queryClient.invalidateQueries).toHaveBeenCalledWith({
      queryKey: $apiStockSheet.queryOptions("get", "/api/portfolio/list")
        .queryKey,
    });
  });
});
