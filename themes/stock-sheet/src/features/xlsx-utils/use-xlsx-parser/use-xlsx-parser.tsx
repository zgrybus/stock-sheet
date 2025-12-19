import { useCallback, useState } from "react";
import { useXtbXlsxParser } from "../use-xtb-xlsx-parser/use-xtb-xlsx-parser";
import { toast } from "sonner";

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
      } catch (_e) {
        toast.error("Couldn't parse file. Please try again");
      } finally {
        setIsParsing(false);
      }
    },
    [xtbParse, onParse]
  );

  return { parse, isParsing };
};
