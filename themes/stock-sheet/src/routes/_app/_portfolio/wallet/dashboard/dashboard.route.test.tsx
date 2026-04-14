import { mockAnalyticsPortfolioSummary } from "@/apis/stock-sheet/mocks/get-analytics-portfolio-summary.mock";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { checkPortfolioGuard } from "@/test/guard-test-utils/check-portfolio-guard";
import { mswServer } from "@/test/msw/msw-server";
import { renderApp } from "@/test/test-utils";
import type { MswRequest } from "@/test/types";
import { screen, waitFor } from "@testing-library/react";
import { delay } from "msw";

describe("Route - /wallet/dashboard", () => {
  let analyticsSummaryMsw: Array<MswRequest> = [];

  beforeEach(() => {
    analyticsSummaryMsw = [];

    mswServer.use(
      $mswStockSheetApi.get(
        "/api/analytics/{portfolioId}/summary",
        ({ response, request }) => {
          analyticsSummaryMsw.push(request);
          return response(200).json(mockAnalyticsPortfolioSummary);
        },
      ),
    );
  });

  test("renders skeleton, when summary api is pending", async () => {
    mswServer.use(
      $mswStockSheetApi.get("/api/analytics/{portfolioId}/summary", () =>
        delay(),
      ),
    );
    await renderApp({ to: "/wallet/dashboard" });

    expect(screen.getByTestId("wallet-summary-skeleton")).toBeVisible();
  });

  test("renders wallet summary data", async () => {
    await renderApp({ to: "/wallet/dashboard" });

    await waitFor(() => {
      expect(
        screen.queryByTestId("wallet-summary-skeleton"),
      ).not.toBeInTheDocument();
    });

    expect(await screen.findByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny90.00%Pieniądze z zysku10.00%",
    );
    expect(await screen.findByTestId("total-wallet-value")).toHaveTextContent(
      "$10,000.00Aktualna wartość Twoich udziałów na podstawie kursów giełdowych",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$9,000.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych.",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$1,000.00+11.11%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$100.00+1.01%dzisiaj",
    );
  });

  checkPortfolioGuard({ to: "/wallet/dashboard" });
});
