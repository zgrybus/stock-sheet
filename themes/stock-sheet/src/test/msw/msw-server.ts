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
);
