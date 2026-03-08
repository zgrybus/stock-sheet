import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { Button } from "@/components/ui/button";
import { DialogFooter } from "@/components/ui/dialog";
import { isErrorDTO } from "@/features/error-response-utils/error-response-utils/error-response-utils";
import { PortfolioErrorType } from "@/features/error-response-utils/types";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { toast } from "sonner";
import { match, P } from "ts-pattern";

type DeletePortfolioDialogContentLazyProps = {
  portfolioId: number;
};

export const DeletePortfolioDialogContentLazy = ({
  portfolioId,
}: DeletePortfolioDialogContentLazyProps) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { data: portfolio } = $apiStockSheet.useSuspenseQuery(
    "get",
    "/api/portfolio/{id}",
    { params: { path: { id: portfolioId } } },
  );

  const { mutateAsync, isPending } = $apiStockSheet.useMutation(
    "delete",
    "/api/portfolio/{id}",
  );

  const onDelete = async () => {
    try {
      await mutateAsync({ params: { path: { id: portfolioId } } });
      await queryClient.invalidateQueries({
        queryKey: $apiStockSheet.queryOptions("get", "/api/portfolio/list")
          .queryKey,
      });

      navigate({
        to: ".",
        search: (prev) => {
          if (prev.portfolioId === portfolioId) {
            return {
              ...prev,
              portfolioId: undefined,
              deletePortfolioId: undefined,
            };
          }
          return { ...prev, deletePortfolioId: undefined };
        },
      });
      toast.success(
        `Portfolio "${portfolio.name}" zostało pomyślnie usunięte.`,
      );
    } catch (e) {
      match(e)
        .with(
          P.when(isErrorDTO),
          ({ errors = [] }) =>
            errors.some(
              ({ type }) => type === PortfolioErrorType.PORTFOLIO_NOT_FOUND,
            ),
          () =>
            toast.error(
              "Nie możemy znaleźć tego portfolio. Prawdopodobnie zostało już wcześniej usunięte.",
            ),
        )
        .otherwise(() => {
          toast.error(`Nie udało się usunąć portfolio "${portfolio.name}".`);
        });
    }
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
