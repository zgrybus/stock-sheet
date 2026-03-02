import type {
  RegisteredRouter,
  ValidateNavigateOptions,
} from "@tanstack/react-router";
import { renderApp } from "../test-utils";
import { mswServer } from "../msw/msw-server";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { mockPortfolioList } from "@/apis/stock-sheet/mocks/get-portfolio-list.mock";
import { mockPortfolio } from "@/apis/stock-sheet/mocks/get-portfolio.mock";

export function checkRoutePortfolio<
  TOptions,
  TRouter extends RegisteredRouter = RegisteredRouter,
>(targetPage: ValidateNavigateOptions<TRouter, TOptions>) {
  async function setup() {
    return renderApp(targetPage);
  }

  describe("Route Portfolio", () => {
    beforeEach(() => {
      mswServer.use(
        $mswStockSheetApi.get("/api/portfolio/list", ({ response }) =>
          response(200).json(mockPortfolioList),
        ),
        $mswStockSheetApi.get("/api/portfolio/{id}", ({ response }) =>
          response(200).json(mockPortfolio),
        ),
      );
    });
  });
}
