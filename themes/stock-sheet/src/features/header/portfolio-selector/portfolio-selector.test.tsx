import {
  renderApp,
  renderComponentWithRouterAndProviders,
} from "@/test/test-utils";
import { screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PortfolioSelector } from "./portfolio-selector";

describe("PortfolioSelector", () => {
  test("opens popover and display list of portfolios", async () => {
    const user = userEvent.setup();
    await renderComponentWithRouterAndProviders(<PortfolioSelector />, {
      to: "/",
    });

    await user.click(screen.getByRole("button", { name: "Wybierz portfel" }));

    const popoverContent = within(
      screen.getByRole("dialog", { name: "Wybór portfela" }),
    );

    const portfolios = popoverContent.getAllByRole("button");
    expect(portfolios).toHaveLength(3);
    expect(portfolios[0]).toHaveTextContent("Portfel REIT");
    expect(portfolios[1]).toHaveTextContent("Portfel Polski");
    expect(portfolios[2]).toHaveTextContent("Akcje USA");
  });

  test("redirects to the /create-portfolio page", async () => {
    const user = userEvent.setup();
    const { router } = await renderApp({
      to: "/",
    });

    expect(router.history.location.href).toBe("/");

    await user.click(screen.getByRole("button", { name: "Wybierz portfel" }));

    const popoverContent = within(
      screen.getByRole("dialog", { name: "Wybór portfela" }),
    );
    await user.click(
      popoverContent.getByRole("link", { name: "Dodaj nowy portfel" }),
    );
    expect(router.history.location.href).toBe("/create-portfolio");
  });
});
