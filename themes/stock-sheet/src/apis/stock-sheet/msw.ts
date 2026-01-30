import { createOpenApiHttp } from "openapi-msw";
import type { paths } from "./generated/client";

export const $mswStockSheetApi = createOpenApiHttp<paths>({
  baseUrl: import.meta.env.VITE_STOCK_SHEET_SERVICE_URL,
});
