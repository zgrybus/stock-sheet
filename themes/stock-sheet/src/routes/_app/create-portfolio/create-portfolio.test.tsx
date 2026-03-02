import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { mockErrorResponse } from "@/apis/stock-sheet/mocks/get-error-response.mock";
import { mockPortfolioList } from "@/apis/stock-sheet/mocks/get-portfolio-list.mock";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { mswServer } from "@/test/msw/msw-server";
import { renderApp } from "@/test/test-utils";
import type { MswRequest } from "@/test/types";
import { QueryClient } from "@tanstack/react-query";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { produce } from "immer";

describe("Route - /create-portfolio", () => {
  let importOperationsMsw: Array<MswRequest> = [];

  beforeEach(() => {
    importOperationsMsw = [];

    mswServer.use(
      $mswStockSheetApi.post("/api/portfolio", ({ response, request }) => {
        importOperationsMsw.push(request);
        return response(200).json({
          id: 1002,
          name: "New Portfolio for Create Portfolio Test",
          currency: "USD",
        });
      }),
    );
  });

  test("submits the form, shows success toast and invalidates portfolio list query", async () => {
    const user = userEvent.setup();
    const queryClient = new QueryClient();
    vi.spyOn(queryClient, "invalidateQueries");
    const { router } = await renderApp(
      { to: "/create-portfolio" },
      { queryClient },
    );

    expect(router.history.location.href).toBe("/create-portfolio");

    await user.click(
      screen.getByRole("combobox", {
        name: "Waluta bazowa",
      }),
    );
    await user.click(
      await screen.findByRole("option", { name: "USD - Dolar amerykański" }),
    );
    await user.type(
      screen.getByRole("textbox", { name: "Nazwa portfela" }),
      "Test porftolio",
    );

    mswServer.use(
      $mswStockSheetApi.get("/api/portfolio/list", ({ response }) =>
        response(200).json(
          produce(mockPortfolioList, (draft) => {
            draft.push({
              id: 1002,
              name: "New Portfolio for Create Portfolio Test",
              currency: "GBP",
            });
          }),
        ),
      ),
    );

    await user.click(screen.getByRole("button", { name: "Utwórz portfel" }));

    expect(
      await screen.findByRole("region", {
        name: (_, element) => {
          return (
            element.textContent ===
            `Portfel "New Portfolio for Create Portfolio Test" został utworzony`
          );
        },
      }),
    ).toBeVisible();

    expect(importOperationsMsw).toHaveLength(1);
    expect(await importOperationsMsw[0].json()).toEqual({
      currency: "USD",
      name: "Test porftolio",
    });
    expect(router.history.location.href).toBe("/?portfolioId=1002");
    expect(queryClient.invalidateQueries).toHaveBeenCalledWith({
      queryKey: $apiStockSheet.queryOptions("get", "/api/portfolio/list")
        .queryKey,
    });
  });

  test("submits the form and shows error toast, when error happened", async () => {
    mswServer.use(
      $mswStockSheetApi.post("/api/portfolio", ({ response, request }) => {
        importOperationsMsw.push(request);
        return response(500).json(mockErrorResponse);
      }),
    );
    const user = userEvent.setup();
    const { router } = await renderApp({ to: "/create-portfolio" });

    expect(router.history.location.href).toBe("/create-portfolio");

    await user.click(
      screen.getByRole("combobox", {
        name: "Waluta bazowa",
      }),
    );
    await user.click(
      await screen.findByRole("option", { name: "USD - Dolar amerykański" }),
    );
    await user.type(
      screen.getByRole("textbox", { name: "Nazwa portfela" }),
      "Test porftolio",
    );

    await user.click(screen.getByRole("button", { name: "Utwórz portfel" }));

    expect(
      await screen.findByRole("region", {
        name: (_, element) => {
          return (
            element.textContent ===
            `Błąd podczas tworzenia portfela. Spróbuj ponownie.`
          );
        },
      }),
    ).toBeVisible();

    expect(importOperationsMsw).toHaveLength(1);
    expect(router.history.location.href).toBe("/create-portfolio");
  });

  describe("Validation", () => {
    describe("Name", () => {
      test("shows error when field is empty", async () => {
        const user = userEvent.setup();
        await renderApp({ to: "/create-portfolio" });

        await user.click(
          screen.getByRole("button", { name: "Utwórz portfel" }),
        );

        expect(screen.getAllByRole("alert")[0]).toHaveTextContent(
          "Nazwa jest wymagana",
        );
      });
    });

    describe("Currency", () => {
      test("shows error when field is not selected", async () => {
        const user = userEvent.setup();
        await renderApp({ to: "/create-portfolio" });

        await user.click(
          screen.getByRole("button", { name: "Utwórz portfel" }),
        );

        expect(screen.getAllByRole("alert")[1]).toHaveTextContent(
          "Wybierz walutę",
        );
      });
    });
  });
});
