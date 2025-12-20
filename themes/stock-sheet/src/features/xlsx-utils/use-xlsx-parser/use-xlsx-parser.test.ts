import { renderHook, screen } from "@testing-library/react";
import { useXlsxParser } from "./use-xlsx-parser"; // Dostosuj ścieżkę
import { useXtbXlsxParser } from "../use-xtb-xlsx-parser/use-xtb-xlsx-parser";
import { useMinimumLoadingTime } from "@/features/loading-utils/use-minimum-loading-time/use-minimum-loading-time";
import { ParseError } from "../types";
import { describe, test, expect, vi, beforeEach } from "vitest";
import { mockCashOperationHistory } from "./xlsx-parser-data.mock";
import { TestProviders } from "@/test/test-utils";

vi.mock("../use-xtb-xlsx-parser/use-xtb-xlsx-parser", { spy: true });
vi.mock(
  "@/features/loading-utils/use-minimum-loading-time/use-minimum-loading-time",
  { spy: true }
);

describe("useXlsxParser", () => {
  beforeEach(() => {
    vi.mocked(useMinimumLoadingTime).mockReturnValue({
      isLoading: false,
      runWithDelay: vi.fn().mockImplementation((fn) => fn()),
    });
  });

  test("parses file successfully and triggers onParse callback", async () => {
    const onParseMock = vi.fn();
    vi.mocked(useXtbXlsxParser).mockReturnValue({
      parse: vi.fn().mockResolvedValue(mockCashOperationHistory),
    });

    const { result } = renderHook(() =>
      useXlsxParser({ onParse: onParseMock })
    );

    const dummyFile = new File([""], "test.xlsx");
    await result.current.parse(dummyFile);

    expect(onParseMock).toHaveBeenCalledWith(mockCashOperationHistory);
  });

  test("exposes loading state from useMinimumLoadingTime", () => {
    vi.mocked(useMinimumLoadingTime).mockReturnValue({
      isLoading: true,
      runWithDelay: vi.fn(),
    });

    const { result } = renderHook(() => useXlsxParser({ onParse: () => {} }));

    expect(result.current.isParsing).toBe(true);
  });

  describe("Error handling", () => {
    test("displays generic error toast when error is not an instance of Error", async () => {
      const onParseMock = vi.fn();
      vi.mocked(useXtbXlsxParser).mockReturnValue({
        parse: vi.fn().mockRejectedValue("Unknown Error"),
      });

      const { result } = renderHook(
        () => useXlsxParser({ onParse: onParseMock }),
        { wrapper: TestProviders }
      );

      await result.current.parse(new File([""], "test.xlsx"));

      expect(
        await screen.findByText(
          "Niepowodzenie importu. Sprawdź format pliku i spróbuj ponownie."
        )
      ).toBeVisible();
      expect(onParseMock).not.toHaveBeenCalled();
    });

    test(`displays specific error toast for ${ParseError.MissingCashOperationHistory}`, async () => {
      const onParseMock = vi.fn();
      vi.mocked(useXtbXlsxParser).mockReturnValue({
        parse: vi
          .fn()
          .mockRejectedValue(new Error(ParseError.MissingCashOperationHistory)),
      });

      const { result } = renderHook(
        () => useXlsxParser({ onParse: onParseMock }),
        { wrapper: TestProviders }
      );

      await result.current.parse(new File([""], "test.xlsx"));

      expect(
        await screen.findByText(
          "Nieprawidłowy format pliku. Upewnij się, że eksportujesz raport z XTB z historią pozycji."
        )
      ).toBeVisible();
      expect(onParseMock).not.toHaveBeenCalled();
    });

    test(`displays specific error toast for ${ParseError.ParsingError}`, async () => {
      const onParseMock = vi.fn();
      vi.mocked(useXtbXlsxParser).mockReturnValue({
        parse: vi.fn().mockRejectedValue(new Error(ParseError.ParsingError)),
      });

      const { result } = renderHook(
        () => useXlsxParser({ onParse: onParseMock }),
        { wrapper: TestProviders }
      );

      await result.current.parse(new File([""], "test.xlsx"));

      expect(
        await screen.findByText(
          "Format danych jest niezgodny z oczekiwanym. Spróbuj wygenerować raport ponownie."
        )
      ).toBeVisible();
      expect(onParseMock).not.toHaveBeenCalled();
    });

    test(`displays specific error toast for ${ParseError.CurrencyError}`, async () => {
      const onParseMock = vi.fn();
      vi.mocked(useXtbXlsxParser).mockReturnValue({
        parse: vi.fn().mockRejectedValue(new Error(ParseError.CurrencyError)),
      });

      const { result } = renderHook(
        () => useXlsxParser({ onParse: onParseMock }),
        { wrapper: TestProviders }
      );

      await result.current.parse(new File([""], "test.xlsx"));

      expect(
        await screen.findByText(
          "Wykryto nieobsługiwaną walutę. Upewnij się, że importujesz raport z właściwego rachunku."
        )
      ).toBeVisible();
      expect(onParseMock).not.toHaveBeenCalled();
    });

    test("displays fallback error toast for unknown Error types", async () => {
      const onParseMock = vi.fn();
      vi.mocked(useXtbXlsxParser).mockReturnValue({
        parse: vi
          .fn()
          .mockRejectedValue(new Error("Some random network error")),
      });

      const { result } = renderHook(
        () => useXlsxParser({ onParse: onParseMock }),
        { wrapper: TestProviders }
      );

      await result.current.parse(new File([""], "test.xlsx"));

      expect(
        await screen.findByText("Blad formatowania danych. Spróbuj ponownie.")
      ).toBeVisible();
      expect(onParseMock).not.toHaveBeenCalled();
    });
  });
});
