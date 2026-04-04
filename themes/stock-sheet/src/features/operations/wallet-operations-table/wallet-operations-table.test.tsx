import { render, screen, within } from "@testing-library/react";
import { WalletOperationsTable } from "./wallet-operations-table";

const operations = [
  {
    id: "id-1",
    stockSymbol: "AAPL",
    stockExchange: "US",
    type: "BUY" as const,
    volume: 10,
    openDate: "15/05/2023 14:30:00",
    pricePerVolume: 150.5,
    totalPrice: 1505.0,
  },
  {
    id: "id-2",
    stockSymbol: "TSLA",
    stockExchange: "L",
    type: "SELL" as const,
    volume: 5,
    openDate: "16/05/2023 09:15:00",
    pricePerVolume: 200.0,
    totalPrice: 1000.0,
  },
];

const initialProps = {
  currency: "USD",
  operations,
};

describe("WalletOperationsTable", () => {
  test("renders header cell", () => {
    render(<WalletOperationsTable {...initialProps} />);

    const rows = screen.getAllByRole("row");

    expect(rows).toHaveLength(3);

    const headerCells = within(rows[0]).getAllByRole("columnheader");

    expect(headerCells).toHaveLength(7);

    expect(headerCells[0]).toHaveTextContent("ID");
    expect(headerCells[1]).toHaveTextContent("Data");
    expect(headerCells[2]).toHaveTextContent("Instrument");
    expect(headerCells[3]).toHaveTextContent("Typ");
    expect(headerCells[4]).toHaveTextContent("Wolumen");
    expect(headerCells[5]).toHaveTextContent("Cena (jedn.)");
    expect(headerCells[6]).toHaveTextContent("Wartość");
  });

  test("renders exact data in specific rows and columns", () => {
    render(<WalletOperationsTable {...initialProps} />);

    const rows = screen.getAllByRole("row");

    expect(rows).toHaveLength(3);

    const row1Cells = within(rows[1]).getAllByRole("cell");
    expect(row1Cells[0]).toHaveTextContent("id-1");
    expect(row1Cells[1]).toHaveTextContent("15.05.2023, 14:30");
    expect(row1Cells[2]).toHaveTextContent("AAPL");
    expect(row1Cells[3]).toHaveTextContent("BUY");
    expect(row1Cells[4]).toHaveTextContent("10");
    expect(row1Cells[5]).toHaveTextContent("$150.50");
    expect(row1Cells[6]).toHaveTextContent("$1,505.00");

    const row2Cells = within(rows[2]).getAllByRole("cell");
    expect(row2Cells[0]).toHaveTextContent("id-2");
    expect(row2Cells[1]).toHaveTextContent("16.05.2023, 09:15");
    expect(row2Cells[2]).toHaveTextContent("TSLA");
    expect(row2Cells[3]).toHaveTextContent("SELL");
    expect(row2Cells[4]).toHaveTextContent("5");
    expect(row2Cells[5]).toHaveTextContent("$200.00");
    expect(row2Cells[6]).toHaveTextContent("$1,000.00");
  });
});
