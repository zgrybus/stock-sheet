import { screen } from "@testing-library/react";
import { MobileHeader } from "./mobile-header";
import { userEvent } from "@testing-library/user-event";
import { renderComponentWithRouterAndProviders } from "@/test/test-utils";

describe("Mobile Header", () => {
  test("opens moible menu, when user clicks on menu button", async () => {
    const user = userEvent.setup();
    await renderComponentWithRouterAndProviders(<MobileHeader />, { to: "/" });

    expect(
      screen.queryByRole("dialog", { name: "Menu" })
    ).not.toBeInTheDocument();

    await user.click(
      screen.getByRole("button", { name: "Otwórz menu mobilne" })
    );

    expect(screen.getByRole("dialog", { name: "Menu" })).toBeVisible();
  });
});
