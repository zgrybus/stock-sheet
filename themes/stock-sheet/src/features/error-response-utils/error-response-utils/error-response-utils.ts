import type { components } from "@/apis/stock-sheet/generated/client";

type ErrorResponse = components["schemas"]["ErrorResponse"];

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const isErrorDTO = (err: any): err is ErrorResponse =>
  err &&
  typeof err === "object" &&
  err["status"] &&
  Array.isArray(err["errors"]);
