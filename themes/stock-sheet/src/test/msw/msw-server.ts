import { mockPortfolioList } from "@/apis/stock-sheet/mocks/get-portfolio-list.mock";
import { mockPortfolio } from "@/apis/stock-sheet/mocks/get-portfolio.mock";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { setupServer } from "msw/node";

export const mswServer = setupServer(
  $mswStockSheetApi.get(
    "/api/operations/holdings/{portfolioId}",
    ({ response }) => response(200).json({ portfolioId: 100, positions: [] }),
  ),
  $mswStockSheetApi.post(
    "/api/operations/import/{portfolioId}",
    ({ response }) => response(200).json({ added: [], duplicated: [] }),
  ),
  $mswStockSheetApi.get("/api/portfolio/list", ({ response }) =>
    response(200).json(mockPortfolioList),
  ),
  $mswStockSheetApi.post("/api/portfolio", ({ response }) =>
    response(200).json({ id: 0, currency: "", name: "" }),
  ),
  $mswStockSheetApi.get("/api/portfolio/{id}", ({ response }) =>
    response(200).json(mockPortfolio),
  ),
  $mswStockSheetApi.delete("/api/portfolio/{id}", ({ response }) =>
    response(204).empty(),
  ),
);
