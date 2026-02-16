import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

export const useOperationsImportMutation = () => {
  const queryClient = useQueryClient();
  const mutation = $apiStockSheet.useMutation(
    "post",
    "/api/operations/import/{portfolioId}",
  );

  const mutate = (
    variables: Parameters<(typeof mutation)["mutate"]>[0],
    options?: Parameters<(typeof mutation)["mutate"]>[1],
  ) => {
    mutation.mutate(variables, {
      onSuccess: (data, _variables, result, context) => {
        const portfolioId = _variables.params.path.portfolioId;
        const { added, duplicated } = data;

        const queryKey = $apiStockSheet.queryOptions(
          "get",
          "/api/operations/holdings/{portfolioId}",
          { params: { path: { portfolioId } } },
        ).queryKey;

        queryClient.invalidateQueries({
          queryKey,
        });

        const addedText = `Nowe pozycje: ${added.length}`;
        const duplicatedText =
          duplicated.length > 0
            ? ` | Pominięte duplikaty: ${duplicated.length}`
            : "";

        toast.success("Import zakończony", {
          description: `${addedText}${duplicatedText}`,
        });

        options?.onSuccess?.(data, _variables, result, context);
      },
    });
  };

  return { ...mutation, mutate };
};
