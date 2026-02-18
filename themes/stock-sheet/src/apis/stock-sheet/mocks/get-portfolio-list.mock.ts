import type { components } from "../generated/client";

export const mockPortfolioList: Array<
  components["schemas"]["PortfolioListResponseDTO"]
> = [
  { id: 101, currency: "PLN", name: "Portfolio item 1" },
  { id: 102, currency: "EUR", name: "Portfolio item 2" },
  { id: 103, currency: "USD", name: "Portfolio item 3" },
];
