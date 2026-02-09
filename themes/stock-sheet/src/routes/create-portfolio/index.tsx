import { createFileRoute, Link } from "@tanstack/react-router";
import { ArrowLeft, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Field, FieldDescription, FieldLabel } from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

export const Route = createFileRoute("/create-portfolio/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div className="mx-auto max-w-5xl">
      <Link
        to="/"
        className={`
          mb-8 flex items-center gap-2 text-sm text-muted-foreground
          transition-colors
          hover:text-foreground
        `}
      >
        <ArrowLeft className="size-4" />
        Powrót do pulpitu
      </Link>
      <section className="mb-6">
        <h2
          className={`
            text-2xl font-bold tracking-tight
            md:text-3xl
          `}
        >
          Nowy portfel
        </h2>
        <p
          className={`
            mt-3 text-sm text-muted-foreground
            md:text-base
          `}
        >
          Zdefiniuj parametry swojego nowego portfela inwestycyjnego.
        </p>
      </section>
      <Card>
        <CardContent>
          <Field>
            <FieldLabel htmlFor="name">Nazwa portfela</FieldLabel>
            <Input
              id="name"
              placeholder="np. Portfel Dywidendowy"
              className={`border-muted-foreground/30`}
            />

            <FieldDescription>
              Nazwa pomoże Ci odróżnić ten portfel w menu przełącznika.
            </FieldDescription>
          </Field>
          <Field className="mt-6">
            <FieldLabel htmlFor="currency">Waluta bazowa</FieldLabel>
            <Select>
              <SelectTrigger
                id="currency"
                className={`border-muted-foreground/30`}
              >
                <SelectValue placeholder="Wybierz walutę" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PLN">PLN - Złoty polski</SelectItem>
                <SelectItem value="USD">USD - Dolar amerykański</SelectItem>
                <SelectItem value="EUR">EUR - Euro</SelectItem>
                <SelectItem value="GBP">GBP - Funt brytyjski</SelectItem>
              </SelectContent>
            </Select>
            <FieldDescription>
              Główna waluta, w której wyliczane będą statystyki portfela.
            </FieldDescription>
          </Field>
        </CardContent>
        <CardFooter className="mt-6 justify-end gap-3">
          <Button variant="ghost" size="lg" asChild>
            <Link to="/">Anuluj</Link>
          </Button>
          <Button size="lg">
            <Plus className="size-4" />
            Utwórz portfel
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}
