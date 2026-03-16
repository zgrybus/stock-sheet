import { createFileRoute } from "@tanstack/react-router";
import { WalletSummary } from "@/features/wallet/wallet-summary/wallet-summary";
import { Badge } from "@/components/ui/badge";
import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { match } from "ts-pattern";
import { WalletSummarySkeleton } from "@/features/wallet/wallet-summary-skeleton/wallet-summary-skeleton";

export const Route = createFileRoute("/_app/_portfolio/wallet/dashboard/")({
  component: Index,
});

function Index() {
  const portfolioSearchParamId = Route.useSearch({
    select: (search) => search.portfolioId,
  });
  const { data: portfolio } = $apiStockSheet.useSuspenseQuery(
    "get",
    "/api/portfolio/{id}",
    { params: { path: { id: portfolioSearchParamId as number } } },
  );
  const analyticsSummaryQuery = $apiStockSheet.useQuery(
    "get",
    "/api/analytics/{portfolioId}/summary",
    { params: { path: { portfolioId: portfolio.id } } },
  );

  return (
    <main className="container mx-auto max-w-6xl">
      <div className="mb-6">
        <div className="mb-2 flex items-center gap-3">
          <h2 className="text-3xl font-bold tracking-tight">
            Pulpit portfela -{" "}
          </h2>
          <span className="text-3xl font-bold text-primary">
            {portfolio.name}
          </span>
        </div>
        <p className="text-muted-foreground">
          Szybki rzut oka na Twoje wyniki inwestycyjne dla portfela{" "}
          <Badge variant="secondary" className="text-sm">
            {portfolio.name}
          </Badge>
          .
        </p>
      </div>
      {match(analyticsSummaryQuery)
        .with({ status: "pending" }, () => <WalletSummarySkeleton />)
        // TODO: add ApiFetchError
        .with({ status: "error" }, () => <div>Error..</div>)
        .with({ status: "success" }, ({ data }) => (
          <WalletSummary currency={portfolio.currency} {...data} />
        ))
        .exhaustive()}
    </main>
  );
}
