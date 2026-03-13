import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

export const useOperationsImportMutation = () => {
  const queryClient = useQueryClient();
  const mutation = $apiStockSheet.useMutation(
    "post",
    "/api/operations/{portfolioId}/operations/import",
  );

  const mutateAsync = async (
    variables: Parameters<(typeof mutation)["mutateAsync"]>[0],
  ) => {
    try {
      const data = await mutation.mutateAsync(variables);

      const portfolioId = variables.params.path.portfolioId;
      const { added, duplicated } = data;

      const queryKey = $apiStockSheet.queryOptions(
        "get",
        "/api/operations/{portfolioId}/holdings",
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
    } catch (_e) {
      // TODO: add error handling
      toast.error("Wystąpił błąd podczas importu operacji. Spróbuj ponownie.");
    }
  };

  return { ...mutation, mutateAsync };
};
