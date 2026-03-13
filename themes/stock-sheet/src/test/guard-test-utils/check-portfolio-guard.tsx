import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { mswServer } from "../msw/msw-server";
import { mockPortfolioList } from "@/apis/stock-sheet/mocks/get-portfolio-list.mock";
import { mockPortfolio } from "@/apis/stock-sheet/mocks/get-portfolio.mock";
import { renderApp } from "../test-utils";
import type {
  RegisteredRouter,
  ValidateNavigateOptions,
} from "@tanstack/react-router";
import { mockErrorResponse } from "@/apis/stock-sheet/mocks/get-error-response.mock";
import { produce } from "immer";

export function checkPortfolioGuard<
  TOptions,
  TRouter extends RegisteredRouter = RegisteredRouter,
>(targetPage: ValidateNavigateOptions<TRouter, TOptions>) {
  describe("Portfolio Guard Logic", () => {
    it("redirects to /create-portfolio when the portfolio list is empty", async () => {
      mswServer.use(
        $mswStockSheetApi.get("/api/portfolio", ({ response }) =>
          response(200).json([]),
        ),
      );

      const { router } = await renderApp(targetPage);

      expect(router.state.location.href).toBe("/create-portfolio");
    });

    it("auto-selects the first portfolio from the list if no portfolioId is provided", async () => {
      mswServer.use(
        $mswStockSheetApi.get("/api/portfolio", ({ response }) =>
          response(200).json(mockPortfolioList),
        ),
      );

      const { router } = await renderApp(targetPage);

      expect(router.state.location.href).toBe(
        `${targetPage.to}?portfolioId=${mockPortfolioList[0].id}`,
      );
    });

    it("should redirect to the first portfolio if the provided portfolioId does not exist in the list", async () => {
      mswServer.use(
        $mswStockSheetApi.get("/api/portfolio", ({ response }) =>
          response(200).json(mockPortfolioList),
        ),
      );
      const { router } = await renderApp({
        ...targetPage,
        search: { portfolioId: 3213123213 },
      } as ValidateNavigateOptions<TRouter, TOptions>);

      expect(router.state.location.href).toBe(
        `${targetPage.to}?portfolioId=${mockPortfolioList[0].id}`,
      );
    });

    it("should redirect to /create-portfolio if fetching details returns 404", async () => {
      mswServer.use(
        $mswStockSheetApi.get("/api/portfolio", ({ response }) =>
          response(200).json(mockPortfolioList),
        ),
        $mswStockSheetApi.get("/api/portfolio/{id}", ({ response }) =>
          response(404).json(
            produce(mockErrorResponse, (draft) => {
              draft.status = 404;
            }),
          ),
        ),
      );

      const { router } = await renderApp(targetPage);

      expect(router.state.location.href).toBe("/create-portfolio");
    });

    it("stays on the route and render content when a valid portfolioId is provided", async () => {
      mswServer.use(
        $mswStockSheetApi.get("/api/portfolio", ({ response }) =>
          response(200).json(mockPortfolioList),
        ),
        $mswStockSheetApi.get("/api/portfolio/{id}", ({ response }) =>
          response(200).json(mockPortfolio),
        ),
      );

      const { router } = await renderApp({
        ...targetPage,
        search: { portfolioId: 101 },
      } as ValidateNavigateOptions<TRouter, TOptions>);

      expect(router.state.location.href).toBe(
        `${targetPage.to}?portfolioId=101`,
      );
    });
  });
}
