import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { isErrorDTO } from "@/features/error-response-utils/error-response-utils/error-response-utils";
import { PortfolioErrorType } from "@/features/error-response-utils/types";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { match, P } from "ts-pattern";

export const useDeletePortfolioMutation = () => {
  const queryClient = useQueryClient();

  const mutation = $apiStockSheet.useMutation("delete", "/api/portfolio/{id}");

  const onDelete = async (portfolioId: number, portfolioName: string) => {
    try {
      await mutation.mutateAsync({ params: { path: { id: portfolioId } } });
      await queryClient.invalidateQueries({
        queryKey: $apiStockSheet.queryOptions("get", "/api/portfolio/list")
          .queryKey,
      });

      toast.success(`Portfolio "${portfolioName}" zostało pomyślnie usunięte.`);
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
          toast.error(`Nie udało się usunąć portfolio "${portfolioName}".`);
        });
    }
  };

  return { ...mutation, mutateAsync: onDelete };
};
