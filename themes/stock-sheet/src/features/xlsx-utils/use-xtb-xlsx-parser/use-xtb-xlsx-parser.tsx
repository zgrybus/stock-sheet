import { useCallback } from "react";
import { read, utils } from "xlsx";
import { ParseError } from "../types";

export const useXtbXlsxParser = () => {
  const parse = useCallback((file: File) => {
    return new Promise<Array<unknown>>((resolve, reject) => {
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
          const json = utils.sheet_to_json(sheet);

          return resolve(json);
        } catch (error) {
          return reject(error);
        }
      };

      reader.readAsArrayBuffer(file);
    });
  }, []);

  return { parse };
};
