import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { ArrowLeft, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldLabel,
} from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useForm } from "@tanstack/react-form";
import { z } from "zod";
import { toast } from "sonner";
import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { useQueryClient } from "@tanstack/react-query";

export const Route = createFileRoute("/_app/create-portfolio/")({
  component: RouteComponent,
});

const portfolioSchema = z.object({
  name: z.string().min(1, "Nazwa jest wymagana"),
  currency: z.string().min(1, "Wybierz walutę"),
});

function RouteComponent() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const { mutateAsync } = $apiStockSheet.useMutation("post", "/api/portfolio");

  const form = useForm({
    defaultValues: {
      name: "",
      currency: "",
    },
    validators: {
      onSubmit: portfolioSchema,
    },
    onSubmit: async ({ value }) => {
      try {
        const data = await mutateAsync({ body: value });

        const portfolioListQueryKey = $apiStockSheet.queryOptions(
          "get",
          "/api/portfolio/list",
        ).queryKey;
        await queryClient.invalidateQueries({
          queryKey: portfolioListQueryKey,
        });

        toast.success(`Portfel "${data.name}" został utworzony`);
        navigate({
          to: "/",
          search: (prev) => ({ ...prev, portfolioId: data.id }),
        });
      } catch (_e) {
        // TODO: add error handling
        toast.error("Błąd podczas tworzenia portfela. Spróbuj ponownie.");
      }
    },
  });

  return (
    <div className="mx-auto max-w-5xl">
      <Link
        to="/"
        className={`
          mb-8 flex items-center gap-2 text-sm text-muted-foreground
          transition-colors
          hover:text-foreground
        `}
        search
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
      <form
        onSubmit={(e) => {
          e.preventDefault();
          e.stopPropagation();
          form.handleSubmit();
        }}
      >
        <Card>
          <CardContent>
            <form.Field
              name="name"
              children={(field) => (
                <Field data-invalid={field.state.meta.errors.length > 0}>
                  <FieldLabel htmlFor={field.name}>Nazwa portfela</FieldLabel>
                  <Input
                    id={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    placeholder="np. Portfel Dywidendowy"
                    className={`border-muted-foreground/30`}
                    aria-invalid={field.state.meta.errors.length > 0}
                  />
                  <FieldDescription>
                    Nazwa pomoże Ci odróżnić ten portfel w menu przełącznika.
                  </FieldDescription>
                  <FieldError errors={field.state.meta.errors} />
                </Field>
              )}
            />
            <form.Field
              name="currency"
              children={(field) => (
                <Field
                  className="mt-6"
                  data-invalid={field.state.meta.errors.length > 0}
                >
                  <FieldLabel htmlFor={field.name}>Waluta bazowa</FieldLabel>
                  <Select
                    name={field.name}
                    value={field.state.value}
                    onValueChange={(value) => field.handleChange(value)}
                  >
                    <SelectTrigger
                      id={field.name}
                      aria-invalid={field.state.meta.errors.length > 0}
                      className={`border-muted-foreground/30`}
                    >
                      <SelectValue placeholder="Wybierz walutę" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="PLN">PLN - Złoty polski</SelectItem>
                      <SelectItem value="USD">
                        USD - Dolar amerykański
                      </SelectItem>
                      <SelectItem value="EUR">EUR - Euro</SelectItem>
                      <SelectItem value="GBP">GBP - Funt brytyjski</SelectItem>
                    </SelectContent>
                  </Select>
                  <FieldDescription>
                    Główna waluta, w której wyliczane będą statystyki portfela.
                  </FieldDescription>
                  <FieldError errors={field.state.meta.errors} />
                </Field>
              )}
            />
          </CardContent>
          <CardFooter className="mt-6 justify-end gap-3">
            <Button variant="ghost" size="lg" asChild>
              <Link to="/" search>
                Anuluj
              </Link>
            </Button>

            <form.Subscribe
              selector={(state) => [state.isSubmitting]}
              children={([isSubmitting]) => (
                <Button type="submit" size="lg" loading={isSubmitting}>
                  <Plus className="size-4" />
                  Utwórz portfel
                </Button>
              )}
            />
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}
