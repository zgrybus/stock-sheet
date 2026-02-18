import type { components } from "../generated/client";

// TODO: change PortfolioResponseDTO to PortfolioListResponseDTO
export const mockPortfolioList: Array<
  components["schemas"]["PortfolioResponseDTO"]
> = [
  { id: 101, currency: "PLN", name: "Portfolio item 1" },
  { id: 102, currency: "EUR", name: "Portfolio item 2" },
  { id: 103, currency: "USD", name: "Portfolio item 3" },
];
