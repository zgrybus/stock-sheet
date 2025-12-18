import { Outlet, createRootRouteWithContext } from "@tanstack/react-router";
import type { QueryClient } from "@tanstack/react-query";

type MyRouterContext = {
  queryClient: QueryClient;
};

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: RootComponent,
});

function RootComponent() {
  return <Outlet />;
}
