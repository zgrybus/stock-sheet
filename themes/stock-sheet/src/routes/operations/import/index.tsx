import { useState } from "react";
import { InputFile } from "@/components/ui/input-file";
import { createFileRoute } from "@tanstack/react-router";
import { useForm, useStore } from "@tanstack/react-form";
import { useXlsxParser } from "@/features/xlsx-utils/use-xlsx-parser/use-xlsx-parser";
import { Button } from "@/components/ui/button";
import { ChevronRight, Upload, FileText, Send } from "lucide-react";
import { Stepper } from "@/components/ui/stepper";
import type { StepItem } from "@/components/ui/stepper";
import { WalletOperationsTable } from "@/features/wallet-operations/wallet-operations-table/wallet-operations-table";
import { ConsentAndSubmitOperations } from "@/features/wallet-operations/consent-and-submit-operations/consent-and-submit-operations";
import type { CashOperationHistory } from "@/features/xlsx-utils/types";
import { match } from "ts-pattern";
import { formatISO, parse as parseDate } from "date-fns";
import { useOperationsImportMutation } from "@/features/operations-api/use-operations-import-mutation/use-operations-import-mutation";

const STEPS: Array<StepItem> = [
  { title: "Wgranie pliku", icon: Upload },
  { title: "Weryfikacja", icon: FileText },
  { title: "Wysyłka", icon: Send },
];

export const Route = createFileRoute("/operations/import/")({
  component: Index,
});

function Index() {
  const [currentStep, setCurrentStep] = useState<0 | 1 | 2>(0);
  const { mutate: onOperationsImportMutate, isPending } =
    useOperationsImportMutation();

  const form = useForm({
    defaultValues: {
      cashOperationHistoryJson: null as CashOperationHistory | null,
    },
    onSubmit: ({ value, formApi }) => {
      if (!value.cashOperationHistoryJson) {
        return;
      }
      const { currency, positions } = value.cashOperationHistoryJson;

      const operations = positions.map((position) => ({
        externalId: position.id,
        stockSymbol: position.stockSymbol,
        type: position.type,
        volume: position.volume,
        openDate: formatISO(
          parseDate(position.openDate, "dd/MM/yyyy HH:mm:ss", new Date()),
        ),
        pricePerVolume: position.pricePerVolume,
        totalPrice: position.totalPrice,
      }));

      onOperationsImportMutate(
        {
          params: { path: { currency } },
          body: { operations },
        },
        {
          onSuccess: () => {
            formApi.reset();
            setCurrentStep(0);
          },
        },
      );
    },
  });

  const { isParsing, parse } = useXlsxParser({
    onParse: (data) => {
      form.setFieldValue("cashOperationHistoryJson", data);
      setCurrentStep(1);
    },
  });

  const cashOperationHistory = useStore(
    form.store,
    (state) => state.values.cashOperationHistoryJson,
  );

  return (
    <form
      className="mx-auto max-w-5xl"
      onSubmit={(e) => {
        e.preventDefault();
        e.stopPropagation();
        form.handleSubmit();
      }}
    >
      <Stepper
        aria-label="Stepper importu operacji"
        steps={STEPS}
        currentStep={currentStep}
        onStepClick={setCurrentStep}
      />
      <div className="mb-10 animate-in text-center duration-500 fade-in">
        <h1 className="text-2xl font-bold tracking-tight">
          {match(currentStep)
            .with(0, () => "Import operacji")
            .with(1, () => "Sprawdź poprawność danych")
            .with(2, () => "Potwierdzenie wysyłki")
            .exhaustive(() => null)}
        </h1>
        <p className="mt-2 text-sm text-muted-foreground">
          {match(currentStep)
            .with(
              0,
              () =>
                "Wgraj historię transakcji (XTB), aby zaktualizować portfel.",
            )
            .with(
              1,
              () =>
                `Znaleziono ${
                  cashOperationHistory?.positions.length || 0
                } operacji.`,
            )
            .with(2, () => "Wymagana jest Twoja zgoda przed zapisaniem danych.")
            .exhaustive(() => null)}
        </p>
      </div>

      {match(currentStep)
        .with(0, () => (
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
        ))
        .with(1, () => (
          <div>
            <WalletOperationsTable
              currency={cashOperationHistory?.currency}
              operations={cashOperationHistory?.positions}
            />
            <div className="mt-10 flex justify-end gap-4">
              <Button
                size="lg"
                variant="secondary"
                onClick={() => {
                  form.reset();
                  setCurrentStep(0);
                }}
              >
                Wróć
              </Button>
              <Button size="lg" onClick={() => setCurrentStep(2)}>
                Dane poprawne <ChevronRight />
              </Button>
            </div>
          </div>
        ))
        .with(2, () => (
          <ConsentAndSubmitOperations
            isPending={isPending}
            setCurrentStep={setCurrentStep}
            totalPosition={cashOperationHistory?.positions.length || 0}
          />
        ))
        .exhaustive(() => null)}
    </form>
  );
}
