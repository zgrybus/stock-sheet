export enum ParseError {
  MissingCashOperationHistory = "MissingCashOperationHistory",
  ParsingError = "ParsingError",
  CurrencyError = "CurrencyError",
}

export type CashOperationHistoryPosition = {
  id: string;
  stockSymbol: string;
  type: "BUY";
  volume: number;
  openDate: string;
  pricePerVolume: number;
  totalPrice: number;
};

export type CashOperationHistory = {
  currency: string;
  positions: Array<CashOperationHistoryPosition>;
};
