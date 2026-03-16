import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Wallet,
  PiggyBank,
  TrendingUp,
  Activity,
  TrendingDown,
} from "lucide-react";
import { numberFormatUtil } from "@/features/number-utils/number-format-util/number-format-util";
import { cn } from "@/lib/utils";

const percentFormatter = numberFormatUtil({
  style: "percent",
  maximumFractionDigits: 2,
});

type WalletSummaryProps = {
  currency: string;
  totalValue: number;
  totalIncome: number;
  investedCapital: number;
  todayIncome: number;
};

export const WalletSummary = ({
  currency,
  todayIncome,
  totalIncome,
  totalValue,
  investedCapital,
}: WalletSummaryProps) => {
  const capitalRatio = Math.min((investedCapital / totalValue) * 100, 100);
  const profitRatio = 100 - capitalRatio;

  const totalProfitPercent = (totalIncome / investedCapital) * 100;
  const yesterdayValue = totalValue - todayIncome;
  const todayIncomePercent = (todayIncome / yesterdayValue) * 100;

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
          <CardContent data-testid="total-wallet-value">
            <p className="mb-4 text-3xl font-bold text-amber-400">
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(totalValue)}
            </p>
            <div
              className={`
                mb-2 h-2 w-full overflow-hidden rounded-full bg-emerald-500/20
              `}
            >
              <div
                className={`
                  h-full rounded-full bg-primary transition-all duration-500
                `}
                style={{ width: `${capitalRatio}%` }}
              />
            </div>
            <div className="flex justify-between text-xs">
              <span className="text-primary">
                Kapitał: {percentFormatter.format(capitalRatio / 100)}
              </span>
              <span
                className={cn({
                  "text-emerald-500": totalValue > investedCapital,
                })}
              >
                Zysk: {percentFormatter.format(profitRatio / 100)}
              </span>
            </div>
          </CardContent>
        </Card>

        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between">
            <CardTitle>Zainwestowany kapitał</CardTitle>
            <PiggyBank className="size-4 text-muted-foreground" />
          </CardHeader>
          <CardContent data-testid="total-wallet-invested-capital">
            <p className="mb-4 text-3xl font-bold text-blue-400">
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(investedCapital)}
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
            {totalIncome >= 0 ? (
              <TrendingUp className="size-4 text-emerald-500" />
            ) : (
              <TrendingDown className="size-4 text-red-500" />
            )}
          </CardHeader>
          <CardContent data-testid="total-wallet-income">
            <p
              className={cn("mb-4 text-3xl font-bold", {
                "text-emerald-500": totalIncome >= 0,
                "text-red-500": totalIncome < 0,
              })}
            >
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(totalIncome)}
            </p>
            <p className="flex items-center text-sm text-muted-foreground">
              <Badge
                variant={totalProfitPercent >= 0 ? "outline" : "destructive"}
                className={cn("mr-2", {
                  "border-emerald-500/30 bg-emerald-500/10 text-emerald-500":
                    totalProfitPercent >= 0,
                })}
              >
                {percentFormatter.format(totalProfitPercent / 100)}
              </Badge>
              od początku
            </p>
          </CardContent>
        </Card>

        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between">
            <CardTitle>Zysk dzienny</CardTitle>
            <Activity
              className={cn("size-4", {
                "text-emerald-500": todayIncome >= 0,
                "text-red-500": todayIncome < 0,
              })}
            />
          </CardHeader>
          <CardContent data-testid="today-wallet-income">
            <p
              className={cn("mb-4 text-3xl font-bold", {
                "text-emerald-500": todayIncome >= 0,
                "text-red-500": todayIncome < 0,
              })}
            >
              {numberFormatUtil({
                style: "currency",
                currency,
              }).format(todayIncome)}
            </p>
            <p className="flex items-center text-sm text-muted-foreground">
              <Badge
                variant={todayIncomePercent >= 0 ? "outline" : "destructive"}
                className={cn("mr-2", {
                  "border-emerald-500/30 bg-emerald-500/10 text-emerald-500":
                    todayIncomePercent >= 0,
                })}
              >
                {percentFormatter.format(todayIncomePercent / 100)}
              </Badge>
              dzisiaj
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
