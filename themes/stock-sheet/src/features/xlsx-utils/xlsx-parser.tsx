import { useCallback, useState } from "react";
import { read, utils } from "xlsx";

type UseXlsxFileParseProps = {
  onParse: (data: Array<unknown>) => void;
  onError: () => void;
};

export const useXlsxFileParse = ({
  onError,
  onParse,
}: UseXlsxFileParseProps) => {
  const [isLoading, setIsLoading] = useState(false);

  const parse = useCallback(
    (file: File) => {
      setIsLoading(true);
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const data = e.target?.result;

          const workbook = read(data, { type: "array" });

          const sheetName = workbook.SheetNames[0];
          const sheet = workbook.Sheets[sheetName];

          const jsonData = utils.sheet_to_json(sheet);

          onParse(jsonData);
        } catch (_error) {
          onError();
        } finally {
          setIsLoading(false);
        }
      };

      reader.onerror = () => onError();
      reader.readAsArrayBuffer(file);
    },
    [onError, onParse]
  );

  return { isLoading, parse };
};
