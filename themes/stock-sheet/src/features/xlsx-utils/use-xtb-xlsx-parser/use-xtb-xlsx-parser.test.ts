import { read, utils } from "xlsx";
import { MockXtbXlsxParserData } from "./xtb-xlsx-parser-data.mock";
import { renderHook } from "@testing-library/react";
import { useXtbXlsxParser } from "./use-xtb-xlsx-parser";
import { TestProviders } from "@/test/test-utils";
import { ParseError } from "../types";
import { produce } from "immer";

describe("useXtbXlsxParser", () => {
  test("extracts positions and currency correctly from a valid XTB report", async () => {
    vi.mocked(utils.sheet_to_json).mockReturnValue(MockXtbXlsxParserData);
    vi.mocked(read).mockReturnValue({
      SheetNames: [],
      Sheets: { "CASH OPERATION HISTORY": {} },
    });

    const { result } = renderHook(() => useXtbXlsxParser(), {
      wrapper: TestProviders,
    });

    const dummyFile = new File([""], "test.xlsx");

    await expect(result.current.parse(dummyFile)).resolves.toEqual({
      currency: "USD",
      positions: [
        {
          id: "1000001",
          openDate: "2023-01-15 14:30:00",
          pricePerVolume: 150,
          stockSymbol: "AAPL.US",
          totalPrice: 1500,
          type: "BUY",
          volume: 10,
        },
        {
          id: "1000002",
          openDate: "2023-02-10 10:15:00",
          pricePerVolume: 300.2,
          stockSymbol: "MSFT.US",
          totalPrice: 1651.1,
          type: "BUY",
          volume: 5.5,
        },
      ],
    });
  });

  test("returns an empty structure when the spreadsheet contains no rows", async () => {
    vi.mocked(utils.sheet_to_json).mockReturnValue([]);
    vi.mocked(read).mockReturnValue({
      SheetNames: [],
      Sheets: { "CASH OPERATION HISTORY": {} },
    });

    const { result } = renderHook(() => useXtbXlsxParser(), {
      wrapper: TestProviders,
    });

    const dummyFile = new File([""], "test.xlsx");

    await expect(result.current.parse(dummyFile)).resolves.toEqual({
      currency: "",
      positions: [],
    });
  });

  describe("Error handling", () => {
    test(`rejects with ${ParseError.MissingCashOperationHistory} when the "CASH OPERATION HISTORY" sheet is missing`, async () => {
      vi.mocked(utils.sheet_to_json).mockReturnValue(MockXtbXlsxParserData);
      vi.mocked(read).mockReturnValue({
        SheetNames: [],
        Sheets: { OTHER: {} },
      });

      const { result } = renderHook(() => useXtbXlsxParser(), {
        wrapper: TestProviders,
      });

      const dummyFile = new File([""], "test.xlsx");

      await expect(result.current.parse(dummyFile)).rejects.toStrictEqual(
        new Error(ParseError.MissingCashOperationHistory)
      );
    });

    test(`rejects with ${ParseError.CurrencyError} when the detected currency is unsupported`, async () => {
      vi.mocked(utils.sheet_to_json).mockReturnValue(
        produce(MockXtbXlsxParserData, (draft) => {
          draft[0].__EMPTY_4 = "USDD";
        })
      );
      vi.mocked(read).mockReturnValue({
        SheetNames: [],
        Sheets: { "CASH OPERATION HISTORY": {} },
      });

      const { result } = renderHook(() => useXtbXlsxParser(), {
        wrapper: TestProviders,
      });

      const dummyFile = new File([""], "test.xlsx");

      await expect(result.current.parse(dummyFile)).rejects.toStrictEqual(
        new Error(ParseError.CurrencyError)
      );
    });

    test(`rejects with ${ParseError.ParsingError} when the positions table header row cannot be found`, async () => {
      vi.mocked(utils.sheet_to_json).mockReturnValue(
        produce(MockXtbXlsxParserData, (draft) => {
          draft[1].__rowNum__ = 10;
        })
      );
      vi.mocked(read).mockReturnValue({
        SheetNames: [],
        Sheets: { "CASH OPERATION HISTORY": {} },
      });

      const { result } = renderHook(() => useXtbXlsxParser(), {
        wrapper: TestProviders,
      });

      const dummyFile = new File([""], "test.xlsx");

      await expect(result.current.parse(dummyFile)).rejects.toStrictEqual(
        new Error(ParseError.ParsingError)
      );
    });

    describe("Stock purchase parsing validation", () => {
      test(`rejects with ${ParseError.ParsingError} when the trade volume is malformed`, async () => {
        vi.mocked(utils.sheet_to_json).mockReturnValue(
          produce(MockXtbXlsxParserData, (draft) => {
            draft[2].__EMPTY_3 = "OPEN BUY _ @ 150.00";
          })
        );
        vi.mocked(read).mockReturnValue({
          SheetNames: [],
          Sheets: { "CASH OPERATION HISTORY": {} },
        });

        const { result } = renderHook(() => useXtbXlsxParser(), {
          wrapper: TestProviders,
        });

        const dummyFile = new File([""], "test.xlsx");

        await expect(result.current.parse(dummyFile)).rejects.toStrictEqual(
          new Error(ParseError.ParsingError)
        );
      });

      test(`rejects with ${ParseError.ParsingError} when the trade price is malformed`, async () => {
        vi.mocked(utils.sheet_to_json).mockReturnValue(
          produce(MockXtbXlsxParserData, (draft) => {
            draft[2].__EMPTY_3 = "OPEN BUY 10 @ _";
          })
        );
        vi.mocked(read).mockReturnValue({
          SheetNames: [],
          Sheets: { "CASH OPERATION HISTORY": {} },
        });

        const { result } = renderHook(() => useXtbXlsxParser(), {
          wrapper: TestProviders,
        });

        const dummyFile = new File([""], "test.xlsx");

        await expect(result.current.parse(dummyFile)).rejects.toStrictEqual(
          new Error(ParseError.ParsingError)
        );
      });

      test(`rejects with ${ParseError.ParsingError} when the trade description format matches no known pattern`, async () => {
        vi.mocked(utils.sheet_to_json).mockReturnValue(
          produce(MockXtbXlsxParserData, (draft) => {
            draft[2].__EMPTY_3 = "NOT 10 @ 105";
          })
        );
        vi.mocked(read).mockReturnValue({
          SheetNames: [],
          Sheets: { "CASH OPERATION HISTORY": {} },
        });

        const { result } = renderHook(() => useXtbXlsxParser(), {
          wrapper: TestProviders,
        });

        const dummyFile = new File([""], "test.xlsx");

        await expect(result.current.parse(dummyFile)).rejects.toStrictEqual(
          new Error(ParseError.ParsingError)
        );
      });
    });
  });
});
