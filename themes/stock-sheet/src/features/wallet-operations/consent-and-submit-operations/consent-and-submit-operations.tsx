import { ShieldCheck, Send, ArrowLeft } from "lucide-react";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";

type ConsentAndSubmitOperationsProps = {
  totalPosition: number;
  setCurrentStep: (step: number) => void;
};

export const ConsentAndSubmitOperations = ({
  totalPosition,
  setCurrentStep,
}: ConsentAndSubmitOperationsProps) => {
  return (
    <div
      className={`
        mx-auto max-w-lg animate-in duration-500 fade-in slide-in-from-bottom-4
      `}
    >
      <Card>
        <CardHeader
          className={`
            text-center
            sm:text-left
          `}
        >
          <div
            className={`
              mb-2 flex flex-col items-center gap-4
              sm:flex-row
            `}
          >
            <div
              className={`
                rounded-full bg-primary/10 p-3 text-primary ring-1
                ring-primary/20
              `}
            >
              <ShieldCheck className="size-6" />
            </div>
            <div>
              <CardTitle className="text-xl">Potwierdzenie wysyłki</CardTitle>
              <CardDescription className="mt-1">
                Wymagana jest Twoja zgoda przed finalnym zapisaniem danych.
              </CardDescription>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          <div
            className={`
              flex justify-between rounded-lg border border-dashed bg-muted/50
              p-4
            `}
          >
            <span className="text-sm text-muted-foreground">
              Liczba pozycji:
            </span>
            <span className="font-mono font-bold text-foreground">
              {totalPosition}
            </span>
          </div>
          <div className={`flex items-start space-x-3 rounded-md border`}>
            <Label htmlFor="terms" className={`cursor-pointer items-start`}>
              <Checkbox
                id="terms"
                className={`
                  mt-1 border-muted-foreground
                  data-[state=checked]:border-primary
                `}
              />
              <div className="text-sm font-semibold">
                <p>Akceptuję warunki importu</p>
                <p className="mt-2 leading-relaxed text-muted-foreground">
                  Potwierdzam, że zweryfikowałem poprawność danych i wyrażam
                  zgodę na ich przetworzenie oraz zapisanie w bazie danych
                  portfela inwestycyjnego.
                </p>
              </div>
            </Label>
          </div>
        </CardContent>

        <CardFooter className="mt-6 flex justify-between gap-4 bg-muted/20">
          <Button
            variant="ghost"
            size="lg"
            onClick={() => setCurrentStep(1)}
            className={`
              text-muted-foreground
              hover:text-foreground
            `}
          >
            <ArrowLeft />
            Wróć
          </Button>
          <Button size="lg" className={`min-w-37.5`}>
            Wyślij dane <Send />
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
};
