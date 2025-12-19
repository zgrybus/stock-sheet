import { InputFile } from "@/components/ui/input-file";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/operations/import/")({
  component: Index,
});

function Index() {
  return (
    <>
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
        <InputFile isLoading={false} onFileSelect={() => {}} />
      </div>
    </>
  );
}
