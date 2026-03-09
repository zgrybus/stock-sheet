import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Wallet, PiggyBank, TrendingUp, Activity } from "lucide-react";
import { numberFormatUtil } from "@/features/number-utils/number-format-util/number-format-util";

const summaryData = {
  currency: "USD",
  totalValue: 6328.9,
  investedCapital: 5508.4,
  totalProfit: 820.5,
  totalProfitPercent: 14.92,
  dailyProfit: 12.3,
  dailyProfitPercent: 0.19,
};

const percentFormatter = numberFormatUtil({
  style: "percent",
  maximumFractionDigits: 0,
});

type WalletSummaryProps = {
  currency: string;
};

export const WalletSummary = ({ currency }: WalletSummaryProps) => {
  const capitalRatio =
    (summaryData.investedCapital / summaryData.totalValue) * 100;

  return (
    <div className="mt-6 flex flex-col gap-4">
      <div
        className={`
          flex flex-col gap-4
          md:flex-row
        `}
      >
        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between">
            <CardTitle>Wartość portfela</CardTitle>
            <Wallet className="size-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <p className="mb-4 text-3xl font-bold text-amber-400">
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(summaryData.totalValue)}
            </p>
            <div
              className={`
                mb-2 h-2 w-full overflow-hidden rounded-full bg-emerald-500/20
              `}
            >
              <div
                className="h-full rounded-full bg-slate-500"
                style={{ width: `${capitalRatio}%` }}
              />
            </div>
            <div
              className={`flex justify-between text-xs text-muted-foreground`}
            >
              <span>
                Kapitał: {percentFormatter.format(capitalRatio / 100)}
              </span>
              <span className="text-emerald-500">
                Zysk: {percentFormatter.format((100 - capitalRatio) / 100)}
              </span>
            </div>
          </CardContent>
        </Card>
        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between">
            <CardTitle>Zainwestowany kapitał</CardTitle>
            <PiggyBank className="size-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <p className="mb-4 text-3xl font-bold text-blue-400">
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(summaryData.investedCapital)}
            </p>
            <p className="text-sm text-muted-foreground">
              Suma wpłaconych przez Ciebie środków.
            </p>
          </CardContent>
        </Card>
      </div>
      <div
        className={`
          flex flex-col gap-4
          md:flex-row
        `}
      >
        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between">
            <CardTitle>Zysk całkowity</CardTitle>
            <TrendingUp className="size-4 text-emerald-500" />
          </CardHeader>
          <CardContent>
            <p className="mb-4 text-3xl font-bold text-emerald-500">
              +
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(summaryData.totalProfit)}
            </p>
            <p className="flex items-center text-sm text-muted-foreground">
              <Badge
                variant="outline"
                className={`
                  mr-2 border-emerald-500/30 bg-emerald-500/10 text-emerald-500
                `}
              >
                +{summaryData.totalProfitPercent}%
              </Badge>
              od początku
            </p>
          </CardContent>
        </Card>
        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between">
            <CardTitle>Zysk dzienny</CardTitle>
            <Activity className="size-4 text-emerald-500" />
          </CardHeader>
          <CardContent>
            <p className="mb-4 text-3xl font-bold text-emerald-500">
              +
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(summaryData.dailyProfit)}
            </p>
            <p className="flex items-center text-sm text-muted-foreground">
              <Badge
                variant="outline"
                className={`
                  mr-2 border-emerald-500/30 bg-emerald-500/10 text-emerald-500
                `}
              >
                +{summaryData.dailyProfitPercent}%
              </Badge>
              dzisiaj
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
