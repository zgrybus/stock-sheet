import { Outlet, createRootRouteWithContext } from "@tanstack/react-router";
import type { QueryClient } from "@tanstack/react-query";
import z from "zod";
import { DeletePortfolioDialog } from "@/features/lazy-modals/delete-portfolio-dialog/delete-portfolio-dialog";

type MyRouterContext = {
  queryClient: QueryClient;
};

const portfolioSearchParam = z.object({
  portfolioId: z.number().optional(),
  deletePortfolioId: z.number().optional(),
});

export const Route = createRootRouteWithContext<MyRouterContext>()({
  validateSearch: portfolioSearchParam,
  component: RootComponent,
  // TODO: add error component with reset
});

function RootComponent() {
  return (
    <>
      <Outlet />
      <DeletePortfolioDialog />
    </>
  );
}
