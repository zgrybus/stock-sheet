import { FileSpreadsheet, UploadCloud } from "lucide-react";
import { Input } from "./input";
import { Label } from "./label";

export const InputFile = () => {
  return (
    <div className="grid w-full items-center gap-1.5">
      <Input
        id="xtb-file-upload"
        type="file"
        accept=".xlsx, .xls, .csv"
        className="hidden"
      />
      <Label
        htmlFor="xtb-file-upload"
        className={`
          group relative flex w-full cursor-pointer flex-col items-center
          justify-center overflow-hidden rounded-lg border-2 border-dashed
          border-muted-foreground/20 bg-muted/5 py-16 transition-all
          duration-200
          hover:border-primary/50 hover:bg-primary/5
        `}
      >
        <div
          className={`
            relative mb-4 rounded-full bg-muted p-4 transition-colors
            group-hover:bg-primary/10
          `}
        >
          <UploadCloud
            className={`
              size-10 text-muted-foreground transition-colors
              group-hover:text-primary
            `}
          />
          <FileSpreadsheet
            className={`
              absolute -right-1 -bottom-1 size-5 rounded-sm bg-background
              text-emerald-500 shadow-sm transition-colors
              group-hover:text-emerald-600
            `}
          />
        </div>
        <span
          className={`mb-2 text-lg font-semibold tracking-tight text-foreground`}
        >
          Upuść raport XTB tutaj
        </span>
        <span
          className={`
            mb-6 max-w-xs text-center text-sm font-normal text-muted-foreground
          `}
        >
          Obsługujemy pliki Excel (.xlsx, .xls) oraz CSV wygenerowane z
          platformy xStation.
        </span>
        <span
          className={`
            text-xs font-medium text-primary underline underline-offset-4
            group-hover:text-primary/80
          `}
        >
          Lub kliknij, aby wybrać plik
        </span>
      </Label>
    </div>
  );
};
