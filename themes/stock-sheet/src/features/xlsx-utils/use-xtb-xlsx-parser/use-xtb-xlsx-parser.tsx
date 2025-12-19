import { useCallback } from "react";
import { read, utils } from "xlsx";

export const useXtbXlsxParser = () => {
  const parse = useCallback((file: File) => {
    return new Promise<Array<unknown>>((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const data = e.target?.result;

          const workbook = read(data, { type: "array" });

          const sheet = workbook.Sheets["CASH OPERATION HISTORY"];
          const json = utils.sheet_to_json(sheet);

          return resolve(json);
        } catch (error) {
          reject(error);
        }
      };

      reader.readAsArrayBuffer(file);
    });
  }, []);

  return { parse };
};
