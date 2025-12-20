import { useCallback } from "react";
import { useXtbXlsxParser } from "../use-xtb-xlsx-parser/use-xtb-xlsx-parser";
import { toast } from "sonner";
import { ParseError } from "../types";
import type { CashOperationHistory } from "../types";
import { useMinimumLoadingTime } from "@/features/loading-utils/use-minimum-loading-time/use-minimum-loading-time";
import { match } from "ts-pattern";

type UseXlsxParserProps = {
  onParse: (data: CashOperationHistory) => void;
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

        match(error.message)
          .with(ParseError.MissingCashOperationHistory, () =>
            toast.error(
              "Nieprawidłowy format pliku. Upewnij się, że eksportujesz raport z XTB z historią pozycji."
            )
          )
          .with(ParseError.ParsingError, () =>
            toast.error(
              "Format danych jest niezgodny z oczekiwanym. Spróbuj wygenerować raport ponownie."
            )
          )
          .with(ParseError.CurrencyError, () =>
            toast.error(
              "Wykryto nieobsługiwaną walutę. Upewnij się, że importujesz raport z właściwego rachunku."
            )
          )
          .otherwise(() =>
            toast.error("Blad formatowania danych. Spróbuj ponownie.")
          );
      }
    },
    [xtbParse, onParse, runWithDelay]
  );

  return { parse, isParsing };
};
