import type { components } from "../generated/client";

export const mockPortfolioHoldings: components["schemas"]["PortfolioHoldingsDTO"] =
  {
    portfolioId: 101,
    positions: [
      { stockSymbol: "NVDA.US", totalCost: 100, totalVolume: 5 },
      { stockSymbol: "TSLA.US", totalCost: 50, totalVolume: 10 },
      { stockSymbol: "MID.US", totalCost: 520, totalVolume: 12 },
    ],
  };
