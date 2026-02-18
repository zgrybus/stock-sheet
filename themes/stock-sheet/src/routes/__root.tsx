import { Outlet, createRootRouteWithContext } from "@tanstack/react-router";
import type { QueryClient } from "@tanstack/react-query";
import z from "zod";

type MyRouterContext = {
  queryClient: QueryClient;
};

const portfolioSearchParam = z.object({
  portfolioId: z.number().optional(),
});

export const Route = createRootRouteWithContext<MyRouterContext>()({
  validateSearch: portfolioSearchParam,
  component: RootComponent,
});

function RootComponent() {
  return <Outlet />;
}
