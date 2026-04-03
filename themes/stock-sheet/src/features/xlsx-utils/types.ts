export enum ParseError {
  MissingCashOperationHistory = "MissingCashOperationHistory",
  ParsingError = "ParsingError",
  CurrencyError = "CurrencyError",
  CurrencyMismatch = "CurrencyMismatch",
}

export type CashOperationHistoryPosition = {
  id: string;
  stockSymbol: string;
  stockExchange: string;
  type: "BUY";
  volume: number;
  openDate: string;
  pricePerVolume: number;
  totalPrice: number;
};

export type CashOperationHistory = {
  positions: Array<CashOperationHistoryPosition>;
};
