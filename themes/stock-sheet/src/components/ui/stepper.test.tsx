import { render, screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";
import { User, Settings, CreditCard } from "lucide-react";
import type { StepItem } from "./stepper";
import { Stepper } from "./stepper";

const steps: Array<StepItem> = [
  { title: "Personal Info", icon: User },
  { title: "Payment", icon: CreditCard },
  { title: "Settings", icon: Settings },
];

const initialProps = {
  steps,
  currentStep: 0,
};

describe("Stepper", () => {
  it("renders all steps as buttons", () => {
    render(<Stepper {...initialProps} />);

    const buttons = screen.getAllByRole("button");
    expect(buttons).toHaveLength(3);

    expect(screen.getByRole("button", { name: "Personal Info" })).toBeVisible();
    expect(screen.getByRole("button", { name: "Payment" })).toBeVisible();
    expect(screen.getByRole("button", { name: "Settings" })).toBeVisible();
  });

  it("renders a Check icon inside the button for completed steps", () => {
    render(<Stepper {...initialProps} currentStep={2} />);

    expect(
      within(screen.getByRole("button", { name: "Personal Info" })).getByTestId(
        "step-item-check"
      )
    ).toBeVisible();
    expect(
      within(screen.getByRole("button", { name: "Payment" })).getByTestId(
        "step-item-check"
      )
    ).toBeVisible();
    expect(
      within(screen.getByRole("button", { name: "Settings" })).queryByTestId(
        "step-item-check"
      )
    ).not.toBeInTheDocument();
  });

  it("renders numbers inside buttons if no icon is provided", () => {
    render(
      <Stepper
        {...initialProps}
        steps={[{ title: "Step One" }, { title: "Step Two" }]}
      />
    );

    expect(
      within(screen.getByRole("button", { name: "1 Step One" })).getByText("1")
    ).toBeVisible();
    expect(
      within(screen.getByRole("button", { name: "2 Step Two" })).getByText("2")
    ).toBeVisible();
  });

  it("calls onStepClick when clicking a clickable button (previous step)", async () => {
    const onStepClickMock = vi.fn();
    const user = userEvent.setup();

    render(
      <Stepper
        {...initialProps}
        currentStep={1}
        onStepClick={onStepClickMock}
      />
    );

    await user.click(screen.getByRole("button", { name: "Personal Info" }));

    expect(onStepClickMock).toHaveBeenCalledWith(0);
  });

  it("disables future step buttons", () => {
    render(<Stepper {...initialProps} />);

    expect(screen.getByRole("button", { name: "Payment" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Settings" })).toBeDisabled();
  });
});
