import { createFileRoute } from "@tanstack/react-router";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { WalletStructureTable } from "@/features/wallet/wallet-structure-table/wallet-structure-table";
import { WalletStructurePieChart } from "@/features/wallet/wallet-structure-pie-chart/wallet-structure-pie-chart";
import { WalletStructureBarChart } from "@/features/wallet/wallet-structure-bar-chart/wallet-structure-bar-chart";
import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { useMemo } from "react";

export const Route = createFileRoute("/_app/_portfolio/wallet/structure/")({
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

  const {
    data: holdings,
    isPending,
    isError,
  } = $apiStockSheet.useQuery(
    "get",
    "/api/operations/{portfolioId}/holdings",
    {
      params: { path: { portfolioId: portfolio.id } },
    },
    { enabled: typeof portfolio.id === "number" },
  );

  const stocks = useMemo(() => {
    if (!holdings?.positions) {
      return [];
    }
    return holdings.positions;
  }, [holdings]);

  if (isPending) {
    // TODO: add skeleton(?)
    return <div>Pobieranie..</div>;
  }

  if (isError) {
    // TODO: add error handling
    return <div>Coś poszło nie tak..</div>;
  }

  return (
    <div className="container mx-auto max-w-6xl">
      <h2 className="mb-2 text-3xl font-bold tracking-tight">
        Struktura portfela
      </h2>
      <p className="text-muted-foreground">
        Poniżej znajduje się szczegółowy wykaz Twoich aktywów, obejmujący
        posiadane wolumeny oraz średnie ceny zakupu.
      </p>
      <Separator className="my-6" />
      <section className="mb-8">
        <h3
          className={`
            mb-2 flex items-center gap-2 text-xl font-semibold tracking-tight
            text-foreground
          `}
        >
          Twoje aktywa
          <Badge variant="default">{stocks.length}</Badge>
        </h3>
        <p className="text-sm text-muted-foreground">
          Lista aktywnych instrumentów w portfelu.
        </p>
      </section>
      <WalletStructureTable stocks={stocks} currency={portfolio.currency} />
      {/* TODO: Do not show charts, when there is no data  */}
      <section className="mt-10 rounded-md border bg-card p-6">
        <h3
          className={`
            text-xl leading-none font-semibold tracking-tight text-foreground
          `}
        >
          Wizualizacja portfela
        </h3>
        <p className="mt-2 text-sm text-muted-foreground">
          Graficzne przedstawienie udziału poszczególnych instrumentów w
          całkowitej wartości Twoich inwestycji.
        </p>
        <WalletStructurePieChart
          stocks={stocks}
          currency={portfolio.currency}
        />
        <WalletStructureBarChart
          stocks={stocks}
          currency={portfolio.currency}
        />
      </section>
    </div>
  );
}
