import { lazy, Suspense } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { useSearch, useNavigate } from "@tanstack/react-router";
import { Spinner } from "@/components/ui/spinner";

const DeletePortfolioDialogContentLazy = lazy(async () => ({
  default: (await import("./delete-portfolio-dialog-content.lazy"))
    .DeletePortfolioDialogContentLazy,
}));

export const DeletePortfolioDialog = () => {
  const navigate = useNavigate();

  const deletePortfolioId = useSearch({
    from: "__root__",
    select: (search) => search.deletePortfolioId,
  });

  if (typeof deletePortfolioId !== "number") {
    return null;
  }

  const handleOpenChange = () => {
    navigate({
      to: ".",
      search: (prev) => ({
        ...prev,
        deletePortfolioId: undefined,
      }),
    });
  };

  return (
    <Dialog open onOpenChange={handleOpenChange}>
      <DialogContent className={`sm:max-w-107`} showCloseButton={false}>
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">
            Usuń portfolio
          </DialogTitle>
          <DialogDescription className="sr-only">
            Potwierdź usunięcie wybranego portfolio. Ta akcja jest
            nieodwracalna.
          </DialogDescription>
        </DialogHeader>
        <Suspense
          fallback={
            <div className="flex justify-center p-4">
              <Spinner size={"lg"} />
            </div>
          }
        >
          <DeletePortfolioDialogContentLazy portfolioId={deletePortfolioId} />
        </Suspense>
      </DialogContent>
    </Dialog>
  );
};
