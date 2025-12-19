import { useCallback, useState } from "react";
import { useXtbXlsxParser } from "../use-xtb-xlsx-parser/use-xtb-xlsx-parser";
import { toast } from "sonner";
import { ParseError } from "../types";

type UseXlsxParserProps = {
  onParse: (data: Array<unknown>) => void;
};

export const useXlsxParser = ({ onParse }: UseXlsxParserProps) => {
  const [isParsing, setIsParsing] = useState(false);
  const { parse: xtbParse } = useXtbXlsxParser();

  const parse = useCallback(
    async (file: File) => {
      setIsParsing(true);
      try {
        const data = await xtbParse(file);
        onParse(data);
      } catch (error) {
        if (!(error instanceof Error)) {
          toast.error("Nie udało się przetworzyć pliku. Spróbuj ponownie.");
          return;
        }
        if (error.message === ParseError.MissingOpenPosition) {
          toast.error(
            "Brak wymaganych danych (Open Positions) w wybranym pliku."
          );
        }
      } finally {
        setIsParsing(false);
      }
    },
    [xtbParse, onParse]
  );

  return { parse, isParsing };
};
