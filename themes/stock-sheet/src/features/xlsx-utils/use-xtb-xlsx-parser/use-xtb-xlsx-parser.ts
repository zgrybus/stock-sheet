import { useCallback } from "react";
import { read, utils } from "xlsx";
import type { WorkSheet } from "xlsx";
import { ParseError } from "../types";
import type { CashOperationHistory } from "../types";
import { isValidCurrency } from "@/features/number-utils/number-format-util/number-format-util";
import { match } from "ts-pattern";

type XlsxRowUnknownType = {
  __EMPTY_1: "";
  __EMPTY_3: string;
};

type XlsxRowPurchaseType = {
  __EMPTY_1: "Stock purchase";
  __EMPTY_3: `OPEN BUY ${number} @ ${number}`;
};

type XlsxRowSaleType = {
  __EMPTY_1: "Stock sale";
  __EMPTY_3: `CLOSE BUY ${number} @ ${number}`;
};

type XlsxRowCloseTradeType = {
  __EMPTY_1: "close trade";
  __EMPTY_3: `Profit of position #${number}`;
};

export type XtbXlsxRowType = {
  // id
  __EMPTY: string;
  // openDate
  __EMPTY_2: string;
  // coment -> volume && pricePerVolume
  __EMPTY_3: string;
  // stockSymbol
  __EMPTY_4: string;
  // totalPrice
  __EMPTY_5: string;
  __rowNum__: number;
} & (
  | XlsxRowUnknownType
  | XlsxRowPurchaseType
  | XlsxRowSaleType
  | XlsxRowCloseTradeType
);

const parseCurrency = (json: Array<XtbXlsxRowType>): string => {
  const currenyRow = json.find((row) => row["__rowNum__"] === 5);

  if (!currenyRow) {
    throw Error(ParseError.ParsingError);
  }

  const currency = currenyRow.__EMPTY_4;

  if (!isValidCurrency(currency)) {
    throw Error(ParseError.CurrencyError);
  }

  return currency;
};

const parseTradeDetails = (input: string) => {
  const regex = /^OPEN BUY ([\d.]+)(?:\/[\d.]+)? @ ([\d.]+)$/;
  const regexMatch = input.match(regex);

  if (!regexMatch) {
    throw Error("ParsingError");
  }

  return {
    volume: parseFloat(regexMatch[1]),
    pricePerVolume: parseFloat(regexMatch[2]),
  };
};

const parseStockSymbol = (input: string) => {
  const regex = /^(.*)\.([^.]+)$/;
  const regexMatch = input.match(regex);

  // TODO: add different ParseError
  if (!regexMatch || regexMatch.length !== 3) {
    throw Error(ParseError.ParsingError);
  }

  return {
    stockSymbol: regexMatch[1],
    stockExchange: match(regexMatch[2])
      .with("UK", () => "L")
      .otherwise((value) => value),
  };
};

const parsePositions = (
  json: Array<XtbXlsxRowType>,
): CashOperationHistory["positions"] => {
  const positionsTableStartIndex = json.findIndex(
    (row) => row["__rowNum__"] === 11,
  );

  const currency = parseCurrency(json);

  // todo
  if (currency !== "USD") {
    throw new Error(ParseError.CurrencyMismatch);
  }

  if (positionsTableStartIndex < 0) {
    throw new Error(ParseError.ParsingError);
  }

  const positionsTable = json.slice(positionsTableStartIndex, json.length - 1);

  return positionsTable
    .map((row) =>
      match(row)
        .with({ __EMPTY_1: "Stock purchase" }, (stockPurchaseRow) => ({
          id: stockPurchaseRow.__EMPTY,
          type: "BUY" as const,
          openDate: stockPurchaseRow.__EMPTY_2,
          totalPrice: Math.abs(parseFloat(stockPurchaseRow.__EMPTY_5)),
          ...parseTradeDetails(stockPurchaseRow.__EMPTY_3),
          ...parseStockSymbol(stockPurchaseRow.__EMPTY_4),
        }))
        .otherwise(() => null),
    )
    .filter((row) => row !== null);
};

const mapXtbXlsx = (json: Array<XtbXlsxRowType>): CashOperationHistory => ({
  positions: parsePositions(json),
});

export const useXtbXlsxParser = () => {
  const parse = useCallback((file: File) => {
    return new Promise<CashOperationHistory>((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const data = e.target?.result;

          const workbook = read(data, { type: "array" });

          const cashOperationHistorySheet = workbook.Sheets[
            "CASH OPERATION HISTORY"
          ] as WorkSheet | undefined;

          if (!cashOperationHistorySheet) {
            return reject(new Error(ParseError.MissingCashOperationHistory));
          }
          const json = utils.sheet_to_json<XtbXlsxRowType>(
            cashOperationHistorySheet,
            {
              raw: false,
            },
          );

          if (json.length === 0) {
            return resolve({ positions: [] });
          }

          return resolve(mapXtbXlsx(json));
        } catch (error) {
          return reject(error);
        }
      };

      reader.readAsArrayBuffer(file);
    });
  }, []);

  return { parse };
};
