import { mockPortfolioSummary } from "@/apis/stock-sheet/mocks/get-portfolio-summary.mock";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { mswServer } from "@/test/msw/msw-server";
import { renderApp } from "@/test/test-utils";
import { screen, within } from "@testing-library/react";

describe("Route /wallet/structure/", () => {
  beforeEach(() => {
    mswServer.use(
      $mswStockSheetApi.get(
        "/api/operations/portfolio/{currency}",
        ({ response }) => {
          return response(200).json(mockPortfolioSummary);
        },
      ),
    );
  });

  test("render the portfolio structure with table", async () => {
    await renderApp({ to: "/wallet/structure" });

    expect(
      await screen.findByRole("heading", { name: "Twoje aktywa 3" }),
    ).toBeVisible();

    const table = within(screen.getByRole("table", { name: "Twoje operacje" }));
    const [_, ...rows] = table.getAllByRole("row");

    expect(rows).toHaveLength(3);

    const firstRowCells = within(rows[0]).getAllByRole("cell");
    expect(firstRowCells[0]).toHaveTextContent("MID.US");
    expect(firstRowCells[1]).toHaveTextContent("12");
    expect(firstRowCells[2]).toHaveTextContent("520");

    const secondRowCells = within(rows[1]).getAllByRole("cell");
    expect(secondRowCells[0]).toHaveTextContent("NVDA.US");
    expect(secondRowCells[1]).toHaveTextContent("5");
    expect(secondRowCells[2]).toHaveTextContent("100");

    const thirdRowCells = within(rows[2]).getAllByRole("cell");
    expect(thirdRowCells[0]).toHaveTextContent("TSLA.US");
    expect(thirdRowCells[1]).toHaveTextContent("10");
    expect(thirdRowCells[2]).toHaveTextContent("50");
  });
});
