import React from "react";
import { Check } from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "./button";

export type StepItem = {
  title: string;
  icon?: LucideIcon;
  description?: string;
};

type StepperProps = {
  steps: Array<StepItem>;
  currentStep: number;
  onStepClick?: (stepId: number) => void;
} & React.ComponentProps<"nav">;

export const Stepper = ({
  steps,
  currentStep,
  onStepClick,
  className,
  ...rest
}: StepperProps) => {
  return (
    <nav className={cn("mb-24 w-full", className)} {...rest}>
      <ol className="flex w-full items-center">
        {steps.map((step, index) => {
          const isCompleted = currentStep > index;
          const isCurrent = currentStep === index;
          const isClickable = onStepClick && index <= currentStep;
          const isLast = index === steps.length - 1;

          return (
            <React.Fragment key={index}>
              <li className="relative flex flex-col items-center">
                <Button
                  className="flex h-auto flex-col gap-3 py-2"
                  size="sm"
                  variant="ghost"
                  disabled={!isClickable}
                  onClick={() => isClickable && onStepClick(index)}
                >
                  <span
                    className={cn(
                      `
                        rounded-full border-red-200 p-1 text-sm font-medium
                        transition-all duration-300
                        md:p-3
                      `,
                      {
                        "border-primary bg-primary text-primary-foreground group-hover:bg-primary/90":
                          isCompleted,
                        "border-primary bg-background text-primary": isCurrent,
                      }
                    )}
                  >
                    {isCompleted ? (
                      <Check
                        className={`
                          size-4
                          md:size-5
                        `}
                      />
                    ) : step.icon ? (
                      <step.icon
                        className={`
                          size-4
                          md:size-5
                        `}
                      />
                    ) : (
                      index + 1
                    )}
                  </span>
                  <span
                    className={cn(
                      `text-center text-sm transition-colors duration-300`,
                      isCurrent ? "text-primary" : "text-muted-foreground"
                    )}
                  >
                    {step.title}
                  </span>
                </Button>
              </li>
              {!isLast && (
                <div
                  className={`
                    relative mx-1 h-1 flex-1 bg-muted
                    md:mx-2
                  `}
                >
                  <div
                    className={cn(
                      "absolute inset-0 bg-primary transition-all duration-500",
                      currentStep > index ? "w-full" : "w-0"
                    )}
                  />
                </div>
              )}
            </React.Fragment>
          );
        })}
      </ol>
    </nav>
  );
};
