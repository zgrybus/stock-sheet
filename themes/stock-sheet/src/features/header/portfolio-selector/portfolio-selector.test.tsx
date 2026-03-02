import {
  renderApp,
  renderComponentWithRouterAndProviders,
} from "@/test/test-utils";
import { screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PortfolioSelector } from "./portfolio-selector";
import { mswServer } from "@/test/msw/msw-server";
import { $mswStockSheetApi } from "@/apis/stock-sheet/msw";
import { mockPortfolioList } from "@/apis/stock-sheet/mocks/get-portfolio-list.mock";

describe("PortfolioSelector", () => {
  beforeEach(() => {
    mswServer.use(
      $mswStockSheetApi.get("/api/portfolio/list", ({ response }) =>
        response(200).json(mockPortfolioList),
      ),
    );
  });

  test("portfolio selector is closed by default", async () => {
    await renderComponentWithRouterAndProviders(<PortfolioSelector />, {
      to: "/",
    });

    expect(
      screen.queryByRole("dialog", { name: "Wybór portfela" }),
    ).not.toBeInTheDocument();
  });

  test("opens popover and display list of portfolios", async () => {
    const user = userEvent.setup();
    await renderComponentWithRouterAndProviders(<PortfolioSelector />, {
      to: "/",
    });

    await user.click(screen.getByRole("button", { name: "Wybierz portfel" }));

    const popoverContent = within(
      screen.getByRole("dialog", { name: "Wybór portfela" }),
    );

    const portfolios = popoverContent.getAllByRole("link");
    expect(portfolios).toHaveLength(4);
    expect(portfolios[0]).toHaveTextContent("Portfolio item 1");
    expect(portfolios[1]).toHaveTextContent("Portfolio item 2");
    expect(portfolios[2]).toHaveTextContent("Portfolio item 3");
    expect(portfolios[3]).toHaveTextContent("Dodaj nowy portfel");
  });

  test("clicking on portfolio item, changes portfolioId in the url", async () => {
    const user = userEvent.setup();
    const { router } = await renderComponentWithRouterAndProviders(
      <PortfolioSelector />,
      {
        to: "/",
      },
    );

    expect(router.history.location.href).toBe("/");

    await user.click(screen.getByRole("button", { name: "Wybierz portfel" }));

    const popoverContent = within(
      screen.getByRole("dialog", { name: "Wybór portfela" }),
    );

    await user.click(
      popoverContent.getByRole("link", { name: "Portfolio item 3" }),
    );

    expect(router.history.location.href).toBe("/?portfolioId=103");

    expect(
      screen.queryByRole("dialog", { name: "Wybór portfela" }),
    ).not.toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Portfolio item 3" }),
    ).toBeVisible();
  });

  test("redirects to the /create-portfolio page", async () => {
    const user = userEvent.setup();
    const { router } = await renderApp({
      to: "/",
    });

    expect(router.history.location.href).toBe("/?portfolioId=101");

    await user.click(screen.getByRole("button", { name: "Portfolio item 1" }));

    const popoverContent = within(
      screen.getByRole("dialog", { name: "Wybór portfela" }),
    );
    await user.click(
      popoverContent.getByRole("link", { name: "Dodaj nowy portfel" }),
    );
    expect(router.history.location.href).toBe(
      "/create-portfolio?portfolioId=101",
    );
  });
});
