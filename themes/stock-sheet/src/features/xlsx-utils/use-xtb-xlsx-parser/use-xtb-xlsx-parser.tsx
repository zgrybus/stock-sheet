import { useCallback } from "react";
import { read, utils } from "xlsx";
import { ParseError } from "../types";
import type { OpenPositionData } from "../types";

type XlsxRowType = {
  // id
  __EMPTY: string;
  // stockSymbol
  __EMPTY_1: string;
  // type
  __EMPTY_2: "BUY";
  // volume
  __EMPTY_3: string;
  // openDate
  __EMPTY_4: string;
  // pricePerVolume
  __EMPTY_5: string;
  // totalPrice
  __EMPTY_7: string;
  // grossPL
  __EMPTY_14: string;
  __rowNum__: number;
};

const mapXtbXlsx = (json: Array<XlsxRowType>): Array<OpenPositionData> =>
  json.map((row) => ({
    id: row.__EMPTY,
    stockValue: row.__EMPTY_1,
    type: row.__EMPTY_2,
    volume: parseFloat(row.__EMPTY_3.replace("$", "")),
    openDate: row.__EMPTY_4,
    pricePerVolume: parseFloat(row.__EMPTY_5.replace("$", "")),
    totalPrice: parseFloat(row.__EMPTY_7.replace("$", "")),
    grossPL: parseFloat(row.__EMPTY_14),
  }));

export const useXtbXlsxParser = () => {
  const parse = useCallback((file: File) => {
    return new Promise<Array<OpenPositionData>>((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const data = e.target?.result;

          const workbook = read(data, { type: "array" });

          const openPositionSheet = workbook.SheetNames.find((sheet) =>
            sheet.includes("OPEN POSITION")
          );
          if (!openPositionSheet) {
            return reject(new Error(ParseError.MissingOpenPosition));
          }

          const sheet = workbook.Sheets[openPositionSheet];
          const json = utils.sheet_to_json<XlsxRowType>(sheet, {
            raw: false,
          });

          const tableRowIndex = json.findIndex(
            (row) => row["__rowNum__"] === 11
          );

          if (tableRowIndex < 0) {
            return reject(new Error(ParseError.ParsingError));
          }

          const tableJson = json.slice(tableRowIndex, json.length - 1);

          return resolve(mapXtbXlsx(tableJson));
        } catch (error) {
          return reject(error);
        }
      };

      reader.readAsArrayBuffer(file);
    });
  }, []);

  return { parse };
};
