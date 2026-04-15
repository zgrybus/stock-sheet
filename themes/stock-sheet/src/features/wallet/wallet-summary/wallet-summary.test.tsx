import { render, screen } from "@testing-library/react";
import { WalletSummary } from "./wallet-summary";
import { TestProviders } from "@/test/test-utils";

describe("Wallet Summary", () => {
  test("renders correct values for a profitable wallet", () => {
    render(
      <WalletSummary
        currency="USD"
        totalValue={10_000}
        totalIncome={1_000}
        investedCapital={9_000}
        todayIncome={100}
      />,
      { wrapper: TestProviders },
    );

    expect(screen.getByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny90.00%Pieniądze z zysku10.00%",
    );
    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$10,000.00Aktualna wartość Twoich udziałów na podstawie kursów giełdowych.",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$9,000.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych.",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$1,000.00+11.11%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$100.00+1.01%dzisiaj",
    );
  });

  test("renders correct values for a loss-making wallet (loss and capped capital)", () => {
    render(
      <WalletSummary
        currency="USD"
        totalValue={8_000}
        totalIncome={-2_000}
        investedCapital={10_000}
        todayIncome={-500}
      />,
      { wrapper: TestProviders },
    );

    expect(screen.getByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny100.00%Pieniądze z zysku0.00%",
    );
    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$8,000.00Aktualna wartość Twoich udziałów na podstawie kursów giełdowych.",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$10,000.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "-$2,000.00-20.00%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "-$500.00-5.88%dzisiaj",
    );
  });

  test("renders correct values when the wallet breaks even", () => {
    render(
      <WalletSummary
        currency="USD"
        totalValue={5_000}
        totalIncome={0}
        investedCapital={5_000}
        todayIncome={0}
      />,
      { wrapper: TestProviders },
    );

    expect(screen.getByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny100.00%Pieniądze z zysku0.00%",
    );
    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$5,000.00Aktualna wartość Twoich udziałów na podstawie kursów giełdowych.",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$5,000.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych.",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$0.00+0.00%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$0.00+0.00%dzisiaj",
    );
  });

  test("renders correct values for a wallet with an overall loss but a daily profit", () => {
    render(
      <WalletSummary
        currency="USD"
        totalValue={9_000}
        totalIncome={-1_000}
        investedCapital={10_000}
        todayIncome={200}
      />,
      { wrapper: TestProviders },
    );

    expect(screen.getByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny100.00%Pieniądze z zysku0.00%",
    );
    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$9,000.00Aktualna wartość Twoich udziałów na podstawie kursów giełdowych.",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$10,000.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych.",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$1,000.00-10.00%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$200.00+2.27%dzisiaj",
    );
  });

  test("renders correct values when all amounts are exactly 0", () => {
    render(
      <WalletSummary
        currency="USD"
        totalValue={0}
        totalIncome={0}
        investedCapital={0}
        todayIncome={0}
      />,
      { wrapper: TestProviders },
    );

    expect(screen.getByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny0.00%Pieniądze z zysku0.00%",
    );
    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$0.00Aktualna wartość Twoich udziałów na podstawie kursów giełdowych.",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$0.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych.",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$0.00+0.00%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$0.00+0.00%dzisiaj",
    );
  });

  test("renders correct values for a wallet with an overall profit but a daily loss", () => {
    render(
      <WalletSummary
        currency="USD"
        totalValue={12_000}
        totalIncome={2_000}
        investedCapital={10_000}
        todayIncome={-500}
      />,
      { wrapper: TestProviders },
    );

    expect(screen.getByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny83.33%Pieniądze z zysku16.67",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$10,000.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych.",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$2,000.00+20.00%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "-$500.00-4.00%dzisiaj",
    );
  });

  test("renders correct values for massive exponential profit (e.g. huge crypto gain)", () => {
    render(
      <WalletSummary
        currency="USD"
        totalValue={100_000}
        totalIncome={99_000}
        investedCapital={1_000}
        todayIncome={50_000}
      />,
      { wrapper: TestProviders },
    );

    expect(screen.getByTestId("structure-of-capital")).toHaveTextContent(
      "Wkład własny1.00%Pieniądze z zysku99.00%",
    );
    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$100,000.00Aktualna wartość Twoich udziałów na podstawie kursów giełdowych",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent(
      "$1,000.00Suma wszystkich środków wpłaconych na zakup papierów wartościowych.",
    );
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$99,000.00+9,900.00%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$50,000.00+100.00%dzisia",
    );
  });
});
