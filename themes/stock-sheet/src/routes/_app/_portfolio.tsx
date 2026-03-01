import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { isErrorDTO } from "@/features/error-response-utils/error-response-utils/error-response-utils";
import { createFileRoute, Outlet, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/_app/_portfolio")({
  component: PortfolioGuardComponent,
  loaderDeps: ({ search }) => ({ portfolioId: search.portfolioId }),
  loader: async ({ context: { queryClient }, deps: { portfolioId } }) => {
    if (!portfolioId) {
      throw redirect({ to: "/create-portfolio" });
    }

    try {
      return await queryClient.ensureQueryData(
        $apiStockSheet.queryOptions("get", "/api/portfolio/{id}", {
          params: { path: { id: portfolioId } },
        }),
      );
    } catch (err: unknown) {
      if (isErrorDTO(err)) {
        if (err.status === 404) {
          throw redirect({ to: "/create-portfolio" });
        }
      }
      throw err;
    }
  },
});

function PortfolioGuardComponent() {
  return <Outlet />;
}
