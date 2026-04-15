import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Wallet,
  PiggyBank,
  TrendingUp,
  Activity,
  TrendingDown,
  PieChart,
} from "lucide-react";
import {
  formatStockPrice,
  numberFormatUtil,
} from "@/features/number-utils/number-format-util/number-format-util";
import { cn } from "@/lib/utils";

const ratioFormatter = numberFormatUtil({
  style: "percent",
  maximumFractionDigits: 2,
  minimumFractionDigits: 2,
});

const signPercentFormatter = numberFormatUtil({
  style: "percent",
  maximumFractionDigits: 2,
  minimumFractionDigits: 2,
  signDisplay: "always",
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
  const isProfit = totalIncome >= 0;

  const capitalShare = isProfit
    ? totalValue > 0
      ? investedCapital / totalValue
      : 0
    : 1;

  const profitShare = isProfit
    ? totalValue > 0
      ? totalIncome / totalValue
      : 0
    : 0;

  const capitalBarWidth = capitalShare * 100;

  const totalProfitPercent =
    investedCapital > 0 ? totalIncome / investedCapital : 0;
  const yesterdayValue = totalValue - todayIncome;
  const todayIncomePercent =
    yesterdayValue > 0 ? todayIncome / yesterdayValue : 0;

  return (
    <div className="mt-6 flex flex-col gap-6">
      <Card className="flex-1">
        <CardHeader className="flex items-center justify-between pb-2">
          <CardTitle>Struktura Twojego kapitał</CardTitle>
          <PieChart className="size-4 text-muted-foreground" />
        </CardHeader>
        <CardContent data-testid="structure-of-capital">
          <div
            className={`
              mb-3 h-3 w-full overflow-hidden rounded-full bg-emerald-500/20
            `}
          >
            <div
              className={`
                h-full rounded-full bg-primary transition-all duration-500
              `}
              style={{ width: `${capitalBarWidth}%` }}
            />
          </div>

          <div
            className={`
              flex justify-between text-xs font-bold tracking-wider uppercase
            `}
          >
            <div className="flex flex-col gap-1">
              <span className="text-[10px] text-muted-foreground">
                Wkład własny
              </span>
              <span className="text-primary">
                {ratioFormatter.format(capitalShare)}
              </span>
            </div>
            <div className="flex flex-col items-end gap-1">
              <span className="text-[10px] text-muted-foreground">
                Pieniądze z zysku
              </span>
              <span
                className={cn(
                  totalIncome >= 0
                    ? "text-emerald-500"
                    : "text-muted-foreground",
                )}
              >
                {ratioFormatter.format(profitShare)}
              </span>
            </div>
          </div>
        </CardContent>
      </Card>

      <div
        className={`
          flex flex-col gap-4
          md:flex-row
        `}
      >
        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between pb-2">
            <CardTitle>Wartość portfela</CardTitle>
            <Wallet className="size-4 text-muted-foreground" />
          </CardHeader>
          <CardContent data-testid="total-wallet-value">
            <p className="text-3xl font-bold text-amber-400">
              {formatStockPrice(totalValue, currency)}
            </p>
            <p className="mt-2 text-sm text-muted-foreground">
              Aktualna wartość Twoich udziałów na podstawie kursów giełdowych.
            </p>
          </CardContent>
        </Card>

        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between pb-2">
            <CardTitle>Zainwestowany kapitał</CardTitle>
            <PiggyBank className="size-4 text-muted-foreground" />
          </CardHeader>
          <CardContent data-testid="total-wallet-invested-capital">
            <p className="text-3xl font-bold text-blue-400">
              {formatStockPrice(investedCapital, currency)}
            </p>
            <p className="mt-2 text-sm text-muted-foreground">
              Suma wszystkich środków wpłaconych na zakup papierów
              wartościowych.
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
          <CardHeader className="flex items-center justify-between pb-2">
            <CardTitle>Zysk całkowity</CardTitle>
            {totalIncome >= 0 ? (
              <TrendingUp className="size-4 text-emerald-500" />
            ) : (
              <TrendingDown className="size-4 text-red-500" />
            )}
          </CardHeader>
          <CardContent data-testid="total-wallet-income">
            <p
              className={cn(
                "mb-4 text-3xl font-bold",
                totalIncome >= 0 ? `text-emerald-500` : `text-red-500`,
              )}
            >
              {formatStockPrice(totalIncome, currency)}
            </p>
            <div className="flex items-center text-sm text-muted-foreground">
              <Badge
                variant={totalProfitPercent >= 0 ? "outline" : "destructive"}
                className={cn("mr-2", {
                  "border-emerald-500/30 bg-emerald-500/10 text-emerald-500":
                    totalProfitPercent >= 0,
                })}
              >
                {signPercentFormatter.format(totalProfitPercent)}
              </Badge>
              od początku
            </div>
          </CardContent>
        </Card>

        <Card className="flex-1">
          <CardHeader className="flex items-center justify-between pb-2">
            <CardTitle>Zysk dzienny</CardTitle>
            <Activity
              className={cn(
                "size-4",
                todayIncome >= 0 ? `text-emerald-500` : `text-red-500`,
              )}
            />
          </CardHeader>
          <CardContent data-testid="today-wallet-income">
            <p
              className={cn(
                "mb-4 text-3xl font-bold",
                todayIncome >= 0 ? `text-emerald-500` : `text-red-500`,
              )}
            >
              {formatStockPrice(todayIncome, currency)}
            </p>
            <div className="flex items-center text-sm text-muted-foreground">
              <Badge
                variant={todayIncomePercent >= 0 ? "outline" : "destructive"}
                className={cn("mr-2", {
                  "border-emerald-500/30 bg-emerald-500/10 text-emerald-500":
                    todayIncomePercent >= 0,
                })}
              >
                {signPercentFormatter.format(todayIncomePercent)}
              </Badge>
              dzisiaj
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
