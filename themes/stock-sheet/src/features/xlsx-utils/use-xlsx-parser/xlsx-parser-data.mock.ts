import type { CashOperationHistory } from "../types";

export const mockCashOperationHistory: CashOperationHistory = {
  currency: "USD",
  positions: [
    {
      id: "1000001",
      stockSymbol: "AAPL.US",
      type: "BUY",
      volume: 10,
      openDate: "15/01/2023 14:30:00",
      pricePerVolume: 150.0,
      totalPrice: 1500.0,
    },
    {
      id: "1000002",
      stockSymbol: "MSFT.US",
      type: "BUY",
      volume: 5.5,
      openDate: "10/02/2023 10:15:00",
      pricePerVolume: 300.2,
      totalPrice: 1651.1,
    },
  ],
};
