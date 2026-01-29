import createFetchClient from "openapi-fetch";
import createClient from "openapi-react-query";
import type { paths } from "./generated/client";

const fetchClient = createFetchClient<paths>({
  baseUrl: import.meta.env.VITE_STOCK_SHEET_SERVICE_URL,
  fetch: (...args) => fetch(...args),
});

export const $apiStockSheet = createClient(fetchClient);
