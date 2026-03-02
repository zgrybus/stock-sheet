import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { isErrorDTO } from "@/features/error-response-utils/error-response-utils/error-response-utils";
import { createFileRoute, Outlet, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/_app/_portfolio")({
  component: PortfolioGuardComponent,
  loaderDeps: ({ search }) => ({ portfolioId: search.portfolioId }),
  loader: async ({ context: { queryClient }, deps }) => {
    const portfolioList = await queryClient.ensureQueryData(
      $apiStockSheet.queryOptions("get", "/api/portfolio/list"),
    );

    if (portfolioList.length === 0) {
      // TODO: redirect to welcome page
      throw redirect({ to: "/create-portfolio" });
    }

    const portfolioExists = portfolioList.some(
      (p) => p.id === deps.portfolioId,
    );

    if (!deps.portfolioId || !portfolioExists) {
      throw redirect({
        to: ".",
        search: (prev) => ({ ...prev, portfolioId: portfolioList[0].id }),
        replace: true,
      });
    }

    try {
      return await queryClient.ensureQueryData(
        $apiStockSheet.queryOptions("get", "/api/portfolio/{id}", {
          params: { path: { id: deps.portfolioId } },
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
