export enum ParseError {
  MissingCashOperationHistory = "MissingCashOperationHistory",
  ParsingError = "ParsingError",
}

export type OpenPositionData = {
  id: string;
  stockSymbol: string;
  type: "BUY";
  volume: number;
  openDate: string;
  pricePerVolume: number;
  totalPrice: number;
};
