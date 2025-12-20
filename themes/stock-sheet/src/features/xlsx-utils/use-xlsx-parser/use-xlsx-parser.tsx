import { useCallback } from "react";
import { useXtbXlsxParser } from "../use-xtb-xlsx-parser/use-xtb-xlsx-parser";
import { toast } from "sonner";
import { ParseError } from "../types";
import type { OpenPositionData } from "../types";
import { useMinimumLoadingTime } from "@/features/loading-utils/use-minimum-loading-time/use-minimum-loading-time";

type UseXlsxParserProps = {
  onParse: (data: Array<OpenPositionData>) => void;
};

export const useXlsxParser = ({ onParse }: UseXlsxParserProps) => {
  const { parse: xtbParse } = useXtbXlsxParser();

  const { isLoading: isParsing, runWithDelay } = useMinimumLoadingTime({
    time: 1000,
  });

  const parse = useCallback(
    async (file: File) => {
      try {
        const data = await runWithDelay(() => xtbParse(file));

        onParse(data);
      } catch (error) {
        if (!(error instanceof Error)) {
          toast.error(
            "Niepowodzenie importu. Sprawdź format pliku i spróbuj ponownie."
          );
          return;
        }

        switch (error.message) {
          case ParseError.MissingCashOperationHistory:
            toast.error(
              "Nieprawidłowy format pliku. Upewnij się, że eksportujesz raport z XTB z historią pozycji."
            );
            break;
          case ParseError.ParsingError:
            toast.error(
              "Format danych jest niezgodny z oczekiwanym. Spróbuj wygenerować raport ponownie."
            );
            break;
          default:
            toast.error("Blad formatowania danych. Spróbuj ponownie.");
        }
      }
    },
    [xtbParse, onParse, runWithDelay]
  );

  return { parse, isParsing };
};
