import { mockPortfolio } from "@/apis/stock-sheet/mocks/get-portfolio.mock";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { mswServer } from "@/test/msw/msw-server";
import { renderComponentWithRouterAndProviders } from "@/test/test-utils";
import type { MswRequest } from "@/test/types";
import { DeletePortfolioDialog } from "./delete-portfolio-dialog";
import { screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

describe("DeletePortfolioDialog", () => {
  let deleteRequestMsw: Array<MswRequest> = [];
  let getPortfolioMsw: Array<MswRequest> = [];

  beforeEach(() => {
    deleteRequestMsw = [];
    getPortfolioMsw = [];

    mswServer.use(
      $mswStockSheetApi.delete(
        "/api/portfolio/{id}",
        ({ response, request }) => {
          deleteRequestMsw.push(request);
          return response(204).empty();
        },
      ),
      $mswStockSheetApi.get("/api/portfolio/{id}", ({ response, request }) => {
        getPortfolioMsw.push(request);
        return response(200).json(mockPortfolio);
      }),
    );
  });

  test("does not render the dialog when deletePortfolioId search param is missing", async () => {
    await renderComponentWithRouterAndProviders(<DeletePortfolioDialog />, {
      to: "/",
    });

    expect(
      screen.queryByRole("dialog", { name: "Usuń portfolio" }),
    ).not.toBeInTheDocument();
  });

  test("displays the delete confirmation dialog when deletePortfolioId is present in URL", async () => {
    await renderComponentWithRouterAndProviders(<DeletePortfolioDialog />, {
      to: "/",
      search: {
        deletePortfolioId: 123123,
      },
    });

    expect(
      screen.getByRole("dialog", { name: "Usuń portfolio" }),
    ).toBeVisible();
  });

  test("close the dialog and clear search params when Anuluj button is clicked", async () => {
    const user = userEvent.setup();

    await renderComponentWithRouterAndProviders(<DeletePortfolioDialog />, {
      to: "/",
      search: {
        deletePortfolioId: 123123,
      },
    });

    const dialog = within(
      screen.getByRole("dialog", { name: "Usuń portfolio" }),
    );

    await user.click(dialog.getByRole("button", { name: "Anuluj" }));

    expect(
      screen.queryByRole("dialog", { name: "Usuń portfolio" }),
    ).not.toBeInTheDocument();
  });

  test("calls delete API and keep current portfolioId in URL when a different portfolio is being deleted", async () => {
    const deletePortfolioId = 9999;
    const portfolioId = 22222;
    const user = userEvent.setup();

    const { router } = await renderComponentWithRouterAndProviders(
      <DeletePortfolioDialog />,
      {
        to: "/",
        search: {
          portfolioId,
          deletePortfolioId,
        },
      },
    );

    expect(router.history.location.href).toBe(
      `/?portfolioId=${portfolioId}&deletePortfolioId=${deletePortfolioId}`,
    );

    const dialog = within(
      screen.getByRole("dialog", { name: "Usuń portfolio" }),
    );

    expect(
      dialog.getByText(
        (_content, element) =>
          element?.textContent ===
          "Czy na pewno chcesz usunąć portfolio Portfolio item 1? Ta akcja jest nieodwracalna i trwale usunie wszystkie powiązane z nim dane.",
      ),
    ).toBeVisible();

    await user.click(dialog.getByRole("button", { name: "Usuń portfolio" }));

    await waitFor(() => expect(deleteRequestMsw).toHaveLength(1));
    expect(deleteRequestMsw[0].url).toContain(
      `/api/portfolio/${deletePortfolioId}`,
    );

    expect(
      screen.queryByRole("dialog", { name: "Usuń portfolio" }),
    ).not.toBeInTheDocument();

    expect(router.history.location.href).toBe(`/?portfolioId=${portfolioId}`);
  });

  test("calls delete API and clear portfolioId from URL when the currently active portfolio is being deleted", async () => {
    const deletePortfolioId = 9999;
    const user = userEvent.setup();

    const { router } = await renderComponentWithRouterAndProviders(
      <DeletePortfolioDialog />,
      {
        to: "/",
        search: {
          portfolioId: deletePortfolioId,
          deletePortfolioId,
        },
      },
    );

    expect(router.history.location.href).toBe(
      `/?portfolioId=${deletePortfolioId}&deletePortfolioId=${deletePortfolioId}`,
    );

    const dialog = within(
      screen.getByRole("dialog", { name: "Usuń portfolio" }),
    );

    expect(
      dialog.getByText(
        (_content, element) =>
          element?.textContent ===
          "Czy na pewno chcesz usunąć portfolio Portfolio item 1? Ta akcja jest nieodwracalna i trwale usunie wszystkie powiązane z nim dane.",
      ),
    ).toBeVisible();

    await user.click(dialog.getByRole("button", { name: "Usuń portfolio" }));

    await waitFor(() => expect(deleteRequestMsw).toHaveLength(1));
    expect(deleteRequestMsw[0].url).toContain(
      `/api/portfolio/${deletePortfolioId}`,
    );

    expect(
      screen.queryByRole("dialog", { name: "Usuń portfolio" }),
    ).not.toBeInTheDocument();

    expect(router.history.location.href).toBe(`/`);
  });
});
