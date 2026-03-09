import { createFileRoute } from "@tanstack/react-router";
import { WalletSummary } from "@/features/wallet/wallet-summary/wallet-summary";

export const Route = createFileRoute("/_app/_portfolio/wallet/dashboard/")({
  component: Index,
});

function Index() {
  return (
    <main className="container mx-auto max-w-6xl">
      <h2 className="mb-2 text-3xl font-bold tracking-tight">
        Pulpit portfela
      </h2>
      <p className="text-muted-foreground">
        Szybki rzut oka na Twoje wyniki inwestycyjne.
      </p>
      <WalletSummary />
    </main>
  );
}
