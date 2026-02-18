import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/_app/_portfolio")({
  component: PortfolioGuardComponent,
  // TODO: add portfolio id, when id is not set in the search params
});

function PortfolioGuardComponent() {
  return <Outlet />;
}
