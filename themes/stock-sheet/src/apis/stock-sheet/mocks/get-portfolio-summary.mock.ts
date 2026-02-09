import type { components } from "../generated/client";

export const mockPortfolioSummary: components["schemas"]["PortfolioSummaryDTO"] =
  {
    currency: "USD",
    positions: [
      { stockSymbol: "NVDA.US", totalCost: 100, totalVolume: 5 },
      { stockSymbol: "TSLA.US", totalCost: 50, totalVolume: 10 },
      { stockSymbol: "MID.US", totalCost: 520, totalVolume: 12 },
    ],
  };
