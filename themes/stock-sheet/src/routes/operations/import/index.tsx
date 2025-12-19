import { InputFile } from "@/components/ui/input-file";
import { createFileRoute } from "@tanstack/react-router";
import { useForm } from "@tanstack/react-form";
import { Field, FieldError } from "@/components/ui/field";
import { useXlsxFileParse } from "@/features/xlsx-utils/xlsx-parser";

export const Route = createFileRoute("/operations/import/")({
  component: Index,
});

function Index() {
  const form = useForm({
    defaultValues: {
      file: "",
    },
    onSubmit: ({ value }) => {
      console.log(value);
    },
  });

  const { isLoading, parse } = useXlsxFileParse({
    onParse: (data) => {
      console.log("data: ", data);
    },
    onError: () => {
      console.log("error");
    },
  });

  return (
    <form>
      <div className="mb-12">
        <h1 className="text-2xl font-bold tracking-tight">Import operacji</h1>
        <p className="mt-2 text-sm text-muted-foreground">
          Wgraj historię transakcji ze swojego konta maklerskiego (XTB), aby
          zaktualizować portfel.
        </p>
      </div>
      <div
        className={`
          mx-auto
          lg:w-125
        `}
      >
        <form.Field
          name="file"
          children={(field) => {
            return (
              <Field data-invalid={field.state.meta.errors.length > 0}>
                <InputFile
                  id={field.name}
                  isLoading={isLoading}
                  onFileSelect={parse}
                />
                <FieldError errors={field.state.meta.errors} />
              </Field>
            );
          }}
        />
      </div>
    </form>
  );
}
