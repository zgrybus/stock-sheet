import { mockCashOperationHistory } from "@/features/xlsx-utils/use-xlsx-parser/xlsx-parser-data.mock";
import { renderApp } from "@/test/test-utils";
import { screen, within } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";

vi.mock(
  import("@/features/xlsx-utils/use-xlsx-parser/use-xlsx-parser"),
  () => ({
    useXlsxParser: ({ onParse }) => ({
      isParsing: false,
      parse: vi.fn().mockImplementation(async () => {
        onParse(mockCashOperationHistory);
        return Promise.resolve();
      }),
    }),
  })
);

describe("Route - /operations/import", () => {
  const mockFile = new File(["dummy content"], "test.xlsx", {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  });

  test("completes the full import flow from file upload to final submission", async () => {
    const user = userEvent.setup();
    await renderApp({ to: "/operations/import" });

    expect(
      screen.getByRole("heading", { name: "Import operacji" })
    ).toBeVisible();
    expect(
      screen.getByText(
        "Wgraj historię transakcji (XTB), aby zaktualizować portfel."
      )
    ).toBeVisible();

    await user.upload(
      screen.getByLabelText(/Upuść raport XTB tutaj/i, {
        selector: "input",
      }),
      mockFile
    );

    expect(
      await screen.findByRole("heading", { name: "Sprawdź poprawność danych" })
    ).toBeVisible();
    expect(screen.getByText("Znaleziono 2 operacji.")).toBeVisible();

    const table = screen.getByRole("table", {
      name: "Historia operacji portfelowych",
    });

    expect(table).toBeVisible();

    const rows = within(table).getAllByRole("row");
    expect(rows).toHaveLength(3);

    await user.click(screen.getByRole("button", { name: "Dane poprawne" }));

    expect(
      await screen.findByRole("heading", { name: "Potwierdzenie wysyłki" })
    ).toBeVisible();
    expect(
      screen.getByText("Wymagana jest Twoja zgoda przed zapisaniem danych.")
    ).toBeVisible();

    expect(
      screen.getByTestId("submit-operations-position-number")
    ).toHaveTextContent("Liczba pozycji:2");

    await user.click(screen.getByRole("button", { name: "Wyślij dane" }));
  });

  test("displays correctly parsed operation details in the review table", async () => {
    const user = userEvent.setup();
    await renderApp({ to: "/operations/import" });

    await user.upload(
      screen.getByLabelText(/Upuść raport XTB tutaj/i, {
        selector: "input",
      }),
      mockFile
    );

    const table = screen.getByRole("table", {
      name: "Historia operacji portfelowych",
    });

    expect(table).toBeVisible();

    const rows = within(table).getAllByRole("row");
    expect(rows).toHaveLength(3);

    const row1Cells = within(rows[1]).getAllByRole("cell");
    expect(row1Cells[0]).toHaveTextContent("1000001");
    expect(row1Cells[1]).toHaveTextContent("15.01.2023, 14:30");
    expect(row1Cells[2]).toHaveTextContent("AAPL.US");
    expect(row1Cells[3]).toHaveTextContent("BUY");
    expect(row1Cells[4]).toHaveTextContent("10");
    expect(row1Cells[5]).toHaveTextContent("$150.00");
    expect(row1Cells[6]).toHaveTextContent("$1,500.00");

    const row2Cells = within(rows[2]).getAllByRole("cell");
    expect(row2Cells[0]).toHaveTextContent("1000002");
    expect(row2Cells[1]).toHaveTextContent("10.02.2023, 10:15");
    expect(row2Cells[2]).toHaveTextContent("MSFT.US");
    expect(row2Cells[3]).toHaveTextContent("BUY");
    expect(row2Cells[4]).toHaveTextContent("5");
    expect(row2Cells[5]).toHaveTextContent("$300.20");
    expect(row2Cells[6]).toHaveTextContent("$1,651.10");
  });

  test("allows returning to the upload screen from the data review step", async () => {
    const user = userEvent.setup();
    await renderApp({ to: "/operations/import" });

    expect(
      screen.getByRole("heading", { name: "Import operacji" })
    ).toBeVisible();

    await user.upload(
      screen.getByLabelText(/Upuść raport XTB tutaj/i, {
        selector: "input",
      }),
      mockFile
    );

    expect(
      await screen.findByRole("heading", { name: "Sprawdź poprawność danych" })
    ).toBeVisible();

    expect(
      screen.getByRole("table", {
        name: "Historia operacji portfelowych",
      })
    ).toBeVisible();
    expect(
      screen.queryByLabelText(/Upuść raport XTB tutaj/i, {
        selector: "input",
      })
    ).not.toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Wróć" }));

    expect(
      await screen.findByRole("heading", { name: "Import operacji" })
    ).toBeVisible();

    expect(
      screen.queryByRole("table", {
        name: "Historia operacji portfelowych",
      })
    ).not.toBeInTheDocument();
    expect(
      screen.queryByLabelText(/Upuść raport XTB tutaj/i, {
        selector: "input",
      })
    ).toBeVisible();
  });

  test("allows returning to the data review table from the confirmation screen", async () => {
    const user = userEvent.setup();
    await renderApp({ to: "/operations/import" });

    expect(
      screen.getByRole("heading", { name: "Import operacji" })
    ).toBeVisible();

    await user.upload(
      screen.getByLabelText(/Upuść raport XTB tutaj/i, {
        selector: "input",
      }),
      mockFile
    );

    expect(
      await screen.findByRole("heading", { name: "Sprawdź poprawność danych" })
    ).toBeVisible();

    expect(
      screen.getByRole("table", {
        name: "Historia operacji portfelowych",
      })
    ).toBeVisible();

    await user.click(screen.getByRole("button", { name: "Dane poprawne" }));

    expect(
      await screen.findByRole("heading", { name: "Potwierdzenie wysyłki" })
    ).toBeVisible();

    expect(
      screen.queryByRole("table", {
        name: "Historia operacji portfelowych",
      })
    ).not.toBeInTheDocument();
    expect(
      screen.getByTestId("submit-operations-position-number")
    ).toBeVisible();

    await user.click(screen.getByRole("button", { name: "Wróć" }));

    expect(
      await screen.findByRole("heading", { name: "Sprawdź poprawność danych" })
    ).toBeVisible();

    expect(
      screen.getByRole("table", {
        name: "Historia operacji portfelowych",
      })
    ).toBeVisible();
    expect(
      screen.queryByTestId("submit-operations-position-number")
    ).not.toBeInTheDocument();
  });
});
