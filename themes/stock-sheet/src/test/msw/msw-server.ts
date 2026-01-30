import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { setupServer } from "msw/node";

export const mswServer = setupServer(
  $mswStockSheetApi.get(
    "/api/operations/portfolio/{currency}",
    ({ response }) => response(200).json({ currency: "", positions: [] }),
  ),
  $mswStockSheetApi.post("/api/operations/import/{currency}", ({ response }) =>
    response(200).json({ added: [], duplicated: [] }),
  ),
);
