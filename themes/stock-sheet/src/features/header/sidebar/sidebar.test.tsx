import { renderComponentWithRouterAndProviders } from "@/test/test-utils";
import { Sidebar } from "./sidebar";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/react";

describe("Sidebar", () => {
  test("redirects to /operations/import, when user clicks on logo", async () => {
    const user = userEvent.setup();
    const { router } = await renderComponentWithRouterAndProviders(
      <Sidebar />,
      { to: "/operations/import" }
    );

    expect(router.state.location.href).toBe("/operations/import");

    await user.click(
      screen.getByRole("link", { name: "Przekieruj na stronę główną" })
    );

    expect(router.state.location.href).toBe("/");
  });

  test("redirects to /operations/import, when user clicks on link", async () => {
    const user = userEvent.setup();
    const { router } = await renderComponentWithRouterAndProviders(
      <Sidebar />,
      { to: "/" }
    );

    expect(router.state.location.href).toBe("/");

    await user.click(screen.getByRole("button", { name: "Operacje" }));
    await user.click(screen.getByRole("link", { name: "Import operacji" }));

    expect(router.state.location.href).toBe("/operations/import");
  });
});
