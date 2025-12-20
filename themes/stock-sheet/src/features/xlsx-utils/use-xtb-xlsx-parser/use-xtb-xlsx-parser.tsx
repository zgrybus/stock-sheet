import { useCallback } from "react";
import { read, utils } from "xlsx";
import type { WorkSheet } from "xlsx";
import { ParseError } from "../types";
import type { CashOperationHistory } from "../types";
import { isValidCurrency } from "@/features/number-utils/number-format-util/number-format-util";

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

type XlsxRowType = {
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
} & (XlsxRowPurchaseType | XlsxRowSaleType | XlsxRowCloseTradeType);

const parseCurrency = (json: Array<XlsxRowType>): string => {
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

const parseTradeDetails = (input: XlsxRowPurchaseType["__EMPTY_3"]) => {
  const regex = /^OPEN BUY (\d+(\.\d+)?) @ (\d+(\.\d+)?)$/;
  const match = input.match(regex);

  if (!match) {
    throw Error(ParseError.ParsingError);
  }

  return {
    volume: parseFloat(match[1]),
    pricePerVolume: parseFloat(match[3]),
  };
};

const parsePositions = (
  json: Array<XlsxRowType>
): CashOperationHistory["positions"] => {
  const positionsTableStartIndex = json.findIndex(
    (row) => row["__rowNum__"] === 11
  );

  if (positionsTableStartIndex < 0) {
    throw new Error(ParseError.ParsingError);
  }

  const positionsTable = json.slice(positionsTableStartIndex, json.length - 1);

  return positionsTable
    .filter((row) => row.__EMPTY_1 === "Stock purchase")
    .map((row) => ({
      id: row.__EMPTY,
      type: "BUY",
      openDate: row.__EMPTY_2,
      stockSymbol: row.__EMPTY_4,
      totalPrice: Math.abs(parseFloat(row.__EMPTY_5)),
      ...parseTradeDetails(row.__EMPTY_3),
    }));
};

const mapXtbXlsx = (json: Array<XlsxRowType>): CashOperationHistory => ({
  currency: parseCurrency(json),
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
          const json = utils.sheet_to_json<XlsxRowType>(
            cashOperationHistorySheet,
            {
              raw: false,
            }
          );

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
