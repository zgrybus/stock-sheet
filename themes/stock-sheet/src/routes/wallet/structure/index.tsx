import { createFileRoute } from "@tanstack/react-router";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { WalletStructureTable } from "@/features/wallet-structure/wallet-structure-table/wallet-structure-table";

export const stocks = [
  {
    name: "AAPL.US",
    volumes: 15,
    averagePrice: 175.5,
    totalPrice: 2632.5,
  },
  {
    name: "MSFT.US",
    volumes: 8,
    averagePrice: 320.1,
    totalPrice: 2560.8,
  },
  {
    name: "NVDA.US",
    volumes: 4,
    averagePrice: 450.0,
    totalPrice: 1800.0,
  },
  {
    name: "GOOGL.US",
    volumes: 20,
    averagePrice: 130.25,
    totalPrice: 2605.0,
  },
  {
    name: "TSLA.US",
    volumes: 10,
    averagePrice: 240.5,
    totalPrice: 2405.0,
  },
  {
    name: "O.US",
    volumes: 50,
    averagePrice: 55.2,
    totalPrice: 2760.0,
  },
];

export const Route = createFileRoute("/wallet/structure/")({
  component: Index,
});

function Index() {
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
        <div className="mb-2 flex items-center gap-2">
          <h3
            className={`text-xl font-semibold tracking-tight text-foreground`}
          >
            Twoje aktywa
          </h3>
          <Badge variant="default">{stocks.length}</Badge>
        </div>
        <p className="text-sm text-muted-foreground">
          Lista aktywnych instrumentów w portfelu.
        </p>
      </section>
      <WalletStructureTable currency={"USD"} stocks={stocks} />
    </div>
  );
}
