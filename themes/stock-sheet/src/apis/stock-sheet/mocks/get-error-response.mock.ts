import type { components } from "../generated/client";

export const mockErrorResponse: components["schemas"]["ErrorResponse"] = {
  status: 500,
  path: "",
  errors: [{ type: "SOMETHING_WENT_WRONG", message: "" }],
};
