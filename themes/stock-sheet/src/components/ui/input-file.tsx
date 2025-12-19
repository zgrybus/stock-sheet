import { useState } from "react";
import { FileSpreadsheet, UploadCloud, Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";

interface InputFileProps {
  isLoading?: boolean;
  onFileSelect: (file: File) => void;
}

export const InputFile = ({ onFileSelect, isLoading }: InputFileProps) => {
  const [isDragging, setIsDragging] = useState(false);

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    if (!isLoading) setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    if (isLoading) return;

    const file = e.dataTransfer.files[0];
    onFileSelect(file);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) onFileSelect(file);
    e.target.value = "";
  };

  return (
    <div className="grid w-full items-center gap-1.5">
      <Input
        id="xtb-file-upload"
        type="file"
        accept=".xlsx, .xls, .csv"
        className="hidden"
        onChange={handleInputChange}
        disabled={isLoading}
      />

      <Label
        htmlFor="xtb-file-upload"
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        className={cn(
          `
            group relative flex w-full cursor-pointer flex-col items-center
            justify-center overflow-hidden rounded-lg border-2 border-dashed
            py-16 transition-all duration-200
          `,
          {
            "cursor-not-allowed border-muted-foreground/10 bg-muted/5 opacity-70":
              isLoading,
            "border-primary bg-primary/10 ring-4 ring-primary/5":
              isDragging && !isLoading,
            "border-muted-foreground/20 bg-muted/5 hover:border-primary/50 hover:bg-primary/5":
              !isDragging && !isLoading,
          }
        )}
      >
        {isLoading ? (
          <div
            className={`
              flex animate-in flex-col items-center gap-4 duration-300 fade-in
              zoom-in
            `}
          >
            <div className="relative mb-4 rounded-full bg-primary/10 p-4">
              <Loader2 className="size-10 animate-spin text-primary" />
            </div>
            <div className="space-y-1 text-center">
              <p className="text-lg font-semibold tracking-tight">
                Przetwarzanie...
              </p>
              <p className="text-sm text-muted-foreground">
                Analizujemy Twój plik XTB
              </p>
            </div>
          </div>
        ) : (
          <>
            <div
              className={cn(
                "relative mb-4 rounded-full bg-muted p-4 transition-colors",
                {
                  "bg-primary/10": isDragging,
                  "group-hover:bg-primary/10": !isDragging,
                }
              )}
            >
              <UploadCloud
                className={cn(
                  "size-10 text-muted-foreground transition-colors",
                  {
                    "text-primary": isDragging,
                    "group-hover:text-primary": !isDragging,
                  }
                )}
              />
              <FileSpreadsheet
                className={cn(
                  `
                    absolute -right-1 -bottom-1 size-5 rounded-sm bg-background
                    text-emerald-500 shadow-sm transition-colors
                  `,
                  {
                    "group-hover:text-emerald-600": !isDragging,
                  }
                )}
              />
            </div>

            <span
              className={`
                mb-2 text-lg font-semibold tracking-tight text-foreground
              `}
            >
              {isDragging ? "Upuść plik teraz" : "Upuść raport XTB tutaj"}
            </span>
            <span
              className={`
                mb-6 max-w-xs text-center text-sm font-normal
                text-muted-foreground
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
          </>
        )}
      </Label>
    </div>
  );
};
