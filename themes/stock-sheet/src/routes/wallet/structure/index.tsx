import { createFileRoute } from "@tanstack/react-router";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { WalletStructureTable } from "@/features/wallet-structure/wallet-structure-table/wallet-structure-table";
import { WalletStructurePieChart } from "@/features/wallet-structure/wallet-structure-pie-chart/wallet-structure-pie-chart";
import { WalletStructureBarChart } from "@/features/wallet-structure/wallet-structure-bar-chart/wallet-structure-bar-chart";
import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { useMemo } from "react";

export const Route = createFileRoute("/wallet/structure/")({
  component: Index,
});

function Index() {
  const currency = "USD";
  const { data, isPending, isError } = $apiStockSheet.useQuery(
    "get",
    "/api/operations/portfolio/{currency}",
    {
      params: { path: { currency } },
    },
  );

  const stocks = useMemo(() => {
    if (!data?.positions) {
      return [];
    }
    return data.positions.sort((a, b) => (a.totalCost > b.totalCost ? -1 : 1));
  }, [data]);

  if (isPending) {
    return <div>Pobieranie..</div>;
  }

  if (isError) {
    return <div>Coś poszło nie tak..</div>;
  }

  return (
    <div className="container mx-auto max-w-5xl">
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
      <WalletStructureTable stocks={stocks} currency={currency} />
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
        <WalletStructurePieChart stocks={stocks} currency={currency} />
        <WalletStructureBarChart stocks={stocks} currency={currency} />
      </section>
    </div>
  );
}
