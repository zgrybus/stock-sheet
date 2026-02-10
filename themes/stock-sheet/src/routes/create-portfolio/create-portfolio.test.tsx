import { renderApp } from "@/test/test-utils";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

describe("Route - /create-portfolio", () => {
  test("submits the form and shows success toast", async () => {
    const user = userEvent.setup();
    await renderApp({ to: "/create-portfolio" });

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
      screen.getByRole("region", {
        name: (_, element) => {
          return (
            element.textContent === `Portfel "Test porftolio" został utworzony`
          );
        },
      }),
    ).toBeVisible();
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
