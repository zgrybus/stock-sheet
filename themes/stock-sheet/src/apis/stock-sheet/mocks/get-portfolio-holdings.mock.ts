import type { components } from "../generated/client";

export const mockPortfolioHoldings: components["schemas"]["PortfolioHoldingsResponseDTO"] =
  {
    portfolioId: 101,
    positions: [
      {
        stockSymbol: "NVDA",
        stockName: "Nvidia",
        totalCost: 100,
        totalVolume: 5,
        stockPrice: 0.2523,
      },
      {
        stockSymbol: "TSLA",
        stockName: "Tesla",
        totalCost: 50,
        totalVolume: 10,
        stockPrice: 74.23,
      },
      {
        stockSymbol: "MID",
        stockName: "Mid Americ Apartments",
        totalCost: 520,
        totalVolume: 12,
        stockPrice: 65,
      },
    ],
  };
