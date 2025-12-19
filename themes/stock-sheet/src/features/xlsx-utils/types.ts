export enum ParseError {
  MissingOpenPosition = "MissingOpenPosition",
  ParsingError = "ParsingError",
}

export type OpenPositionData = {
  id: string;
  stockValue: string;
  type: "BUY";
  volume: number;
  openDate: string;
  pricePerVolume: number;
  totalPrice: number;
  grossPL: number;
};
