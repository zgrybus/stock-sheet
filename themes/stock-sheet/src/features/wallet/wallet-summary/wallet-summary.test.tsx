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

    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$10,000.00Kapitał: 90%Zysk: 10%",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent("$9,000.00Suma wpłaconych przez Ciebie środków.");
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$1,000.0011.11%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$100.001.01%dzisiaj",
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

    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$8,000.00Kapitał: 100%Zysk: 0%",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent("$10,000.00Suma wpłaconych przez Ciebie środków.");
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "-$2,000.00-20%od początku",
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

    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$5,000.00Kapitał: 100%Zysk: 0%",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent("$5,000.00Suma wpłaconych przez Ciebie środków.");
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "$0.000%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$0.000%dzisiaj",
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

    expect(screen.getByTestId("total-wallet-value")).toHaveTextContent(
      "$9,000.00Kapitał: 100%Zysk: 0%",
    );
    expect(
      screen.getByTestId("total-wallet-invested-capital"),
    ).toHaveTextContent("$10,000.00Suma wpłaconych przez Ciebie środków.");
    expect(screen.getByTestId("total-wallet-income")).toHaveTextContent(
      "-$1,000.00-10%od początku",
    );
    expect(screen.getByTestId("today-wallet-income")).toHaveTextContent(
      "$200.002.27%dzisiaj",
    );
  });
});
