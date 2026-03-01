import {
  Outlet,
  createRootRouteWithContext,
  redirect,
} from "@tanstack/react-router";
import type { QueryClient } from "@tanstack/react-query";
import z from "zod";
import { $apiStockSheet } from "@/apis/stock-sheet/client";

type MyRouterContext = {
  queryClient: QueryClient;
};

const portfolioSearchParam = z.object({
  portfolioId: z.number().optional(),
});

export const Route = createRootRouteWithContext<MyRouterContext>()({
  validateSearch: portfolioSearchParam,
  component: RootComponent,
  loaderDeps: ({ search }) => ({ portfolioId: search.portfolioId }),
  loader: async ({ context: { queryClient }, deps }) => {
    if (deps.portfolioId) {
      return;
    }

    const portfolioList = await queryClient.ensureQueryData(
      $apiStockSheet.queryOptions("get", "/api/portfolio/list"),
    );

    if (!portfolioList.length) {
      throw redirect({ to: "/create-portfolio" });
    }

    throw redirect({
      to: ".",
      search: (search) => ({ ...search, portfolioId: portfolioList[0].id }),
    });
  },
  // TODO: add error component with reset
});

function RootComponent() {
  return <Outlet />;
}
