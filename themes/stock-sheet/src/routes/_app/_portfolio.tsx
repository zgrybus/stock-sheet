import { createFileRoute, Outlet } from "@tanstack/react-router";
import z from "zod";

const portfolioSearchParam = z.object({
  portfolioId: z.number().optional(),
});

export const Route = createFileRoute("/_app/_portfolio")({
  validateSearch: portfolioSearchParam,
  component: PortfolioGuardComponent,
  // TODO: add portfolio id, when id is not set in the search params
});

function PortfolioGuardComponent() {
  return <Outlet />;
}
