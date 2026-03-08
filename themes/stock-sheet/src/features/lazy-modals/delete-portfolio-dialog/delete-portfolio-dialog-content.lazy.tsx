import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { Button } from "@/components/ui/button";
import { DialogFooter } from "@/components/ui/dialog";
import { useDeletePortfolioMutation } from "@/features/portfolio-api/use-delete-portfolio-mutation/use-delete-portfolio-mutation";
import { useNavigate } from "@tanstack/react-router";

type DeletePortfolioDialogContentLazyProps = {
  portfolioId: number;
};

export const DeletePortfolioDialogContentLazy = ({
  portfolioId,
}: DeletePortfolioDialogContentLazyProps) => {
  const navigate = useNavigate();

  const { data: portfolio } = $apiStockSheet.useSuspenseQuery(
    "get",
    "/api/portfolio/{id}",
    { params: { path: { id: portfolioId } } },
  );

  const { mutateAsync, isPending } = useDeletePortfolioMutation();

  const onDelete = async () => {
    await mutateAsync(portfolioId, portfolio.name);

    navigate({
      to: ".",
      search: (prev) => ({
        ...prev,
        deletePortfolioId: undefined,
        portfolioId:
          prev.portfolioId === portfolioId ? undefined : prev.portfolioId,
      }),
    });
  };

  const onCancel = () => {
    navigate({
      to: ".",
      search: (prev) => ({
        ...prev,
        deletePortfolioId: undefined,
      }),
    });
  };

  return (
    <>
      <p className="text-sm leading-relaxed">
        Czy na pewno chcesz usunąć portfolio{" "}
        <strong className={`text-foreground`}>{portfolio.name}</strong>?{" "}
        <span className={`text-destructive`}>
          Ta akcja jest nieodwracalna i trwale usunie wszystkie powiązane z nim
          dane.
        </span>
      </p>

      <DialogFooter
        className={`
          flex-col gap-2
          sm:flex-row sm:justify-end sm:gap-x-3
        `}
      >
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          disabled={isPending}
        >
          Anuluj
        </Button>
        <Button
          type="button"
          variant="destructive"
          onClick={onDelete}
          disabled={isPending}
          loading={isPending}
        >
          Usuń portfolio
        </Button>
      </DialogFooter>
    </>
  );
};
