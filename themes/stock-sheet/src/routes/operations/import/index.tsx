import { useState } from "react";
import { InputFile } from "@/components/ui/input-file";
import { createFileRoute } from "@tanstack/react-router";
import { useForm, useStore } from "@tanstack/react-form";
import { useXlsxParser } from "@/features/xlsx-utils/use-xlsx-parser/use-xlsx-parser";
import { Button } from "@/components/ui/button";
import { ChevronRight, Upload, FileText, Send } from "lucide-react";
import { Stepper } from "@/components/ui/stepper";
import type { StepItem } from "@/components/ui/stepper";

type OperationJson = {
  id: string;
  stockValue: string;
  type: "BUY";
  volume: number;
  openDate: string;
  pricePerVolume: number;
  totalPrice: number;
  grossPL: number;
};

const STEPS: Array<StepItem> = [
  { title: "Wgranie pliku", icon: Upload },
  { title: "Weryfikacja", icon: FileText },
  { title: "Wysyłka", icon: Send },
];

export const Route = createFileRoute("/operations/import/")({
  component: Index,
});

function Index() {
  const [currentStep, setCurrentStep] = useState<number>(0);

  const form = useForm({
    defaultValues: {
      operationJson: null as Array<OperationJson> | null,
    },
    onSubmit: ({ value }) => {
      console.log(value);
    },
  });

  const { isParsing, parse } = useXlsxParser({
    onParse: (data) => {
      form.setFieldValue("operationJson", data);
      setCurrentStep(1);
    },
  });

  const operationJson = useStore(
    form.store,
    (state) => state.values.operationJson
  );

  return (
    <div className="mx-auto max-w-4xl">
      <Stepper
        aria-label="Stepper importu operacji"
        steps={STEPS}
        currentStep={currentStep}
        onStepClick={setCurrentStep}
      />
      <div className="animate-in text-center duration-500 fade-in">
        <h1 className="text-2xl font-bold tracking-tight">
          {currentStep === 0 && "Import operacji"}
          {currentStep === 1 && "Sprawdź poprawność danych"}
          {currentStep === 2 && "Potwierdzenie wysyłki"}
        </h1>
        <p className="mt-2 text-sm text-muted-foreground">
          {currentStep === 0 &&
            "Wgraj historię transakcji (XTB), aby zaktualizować portfel."}
          {currentStep === 1 &&
            `Znaleziono ${operationJson?.length || 0} operacji.`}
          {currentStep === 2 &&
            "Wymagana jest Twoja zgoda przed zapisaniem danych."}
        </p>
      </div>

      {currentStep === 0 && (
        <div
          className={`
            mx-auto
            lg:w-125
          `}
        >
          <InputFile
            id="xlsx-file"
            isLoading={isParsing}
            onFileSelect={parse}
          />
        </div>
      )}

      {currentStep === 1 && operationJson && (
        <div className="space-y-4">
          <div className="mt-6 flex justify-between">
            <Button onClick={() => setCurrentStep(2)}>
              Dane poprawne <ChevronRight className="ml-2 h-4 w-4" />
            </Button>
          </div>
        </div>
      )}

      {currentStep === 2 && (
        <div className="mx-auto max-w-md rounded-lg border bg-card p-6">
          <div className="flex justify-between pt-4">
            <Button variant="outline" onClick={() => setCurrentStep(1)}>
              Wróć
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
