import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";
import { ConsentAndSubmitOperations } from "./consent-and-submit-operations";

const initialProps = {
  isPending: false,
  totalPosition: 42,
  setCurrentStep: vi.fn(),
};

describe("ConsentAndSubmitOperations", () => {
  it("renders total positions count correctly", () => {
    render(<ConsentAndSubmitOperations {...initialProps} />);

    expect(
      screen.getByTestId("submit-operations-position-number"),
    ).toHaveTextContent("Liczba pozycji:42");
  });

  it("navigates back to step 1 when button is clicked", async () => {
    const setCurrentStepMock = vi.fn();
    const user = userEvent.setup();

    render(
      <ConsentAndSubmitOperations
        {...initialProps}
        setCurrentStep={setCurrentStepMock}
      />,
    );

    await user.click(screen.getByRole("button", { name: "Wróć" }));

    expect(setCurrentStepMock).toHaveBeenCalledWith(1);
  });

  it("toggles the terms acceptance checkbox when clicked", async () => {
    const user = userEvent.setup();
    render(<ConsentAndSubmitOperations {...initialProps} />);

    const checkbox = screen.getByRole("checkbox", {
      name: /Akceptuję warunki importu/i,
    });

    expect(checkbox).not.toBeChecked();

    await user.click(checkbox);
    expect(checkbox).toBeChecked();

    await user.click(checkbox);
    expect(checkbox).not.toBeChecked();
  });
});
