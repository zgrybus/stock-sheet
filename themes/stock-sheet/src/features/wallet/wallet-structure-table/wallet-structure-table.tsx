import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  formatStockPrice,
  numberFormatUtil,
} from "@/features/number-utils/number-format-util/number-format-util";
import { cn } from "@/lib/utils";

const percentFormatter = numberFormatUtil({
  style: "percent",
  maximumFractionDigits: 2,
  minimumFractionDigits: 2,
  signDisplay: "always",
});

type Operation = {
  totalCost: number;
  stockSymbol: string;
  stockPrice: number;
  stockName: string;
  totalVolume: number;
  averagePrice: number;
  totalProfit: number;
  profitPercentage: number;
};

type WalletStructureTableProps = {
  currency: string;
  stocks: Array<Operation>;
};

export const WalletStructureTable = ({
  currency,
  stocks,
}: WalletStructureTableProps) => {
  return (
    <div className="rounded-md border bg-card shadow-sm">
      <Table aria-label="Twoje operacje">
        <TableHeader className="bg-secondary/50">
          <TableRow className="hover:bg-transparent">
            <TableHead
              className={`
                w-full px-4 py-4 align-middle text-xs font-bold tracking-wider
                text-foreground uppercase
              `}
            >
              Walor
            </TableHead>

            <TableHead
              className={`
                px-4 py-4 text-right align-middle text-xs font-bold
                tracking-wider text-foreground uppercase
              `}
            >
              Wolumeny
            </TableHead>
            <TableHead
              className={`
                px-4 py-4 text-center align-middle text-xs font-bold
                tracking-wider text-foreground uppercase
              `}
            >
              <div className="flex flex-col items-center gap-1 leading-none">
                <span>Aktualna</span>
                <span>Cena</span>
              </div>
            </TableHead>
            <TableHead
              className={`
                px-4 py-4 text-center align-middle text-xs font-bold
                tracking-wider text-foreground uppercase
              `}
            >
              <div className="flex flex-col items-center gap-1 leading-none">
                <span>Średnia</span>
                <span>Cena Zakupu</span>
              </div>
            </TableHead>
            <TableHead
              className={`
                px-4 py-4 text-center align-middle text-xs font-bold
                tracking-wider text-foreground uppercase
              `}
            >
              <div className="flex flex-col items-center gap-1 leading-none">
                <span>Stopa</span>
                <span>Zwrotu</span>
              </div>
            </TableHead>
            <TableHead
              className={`
                px-4 py-4 text-right align-middle text-xs font-bold
                tracking-wider text-foreground uppercase
              `}
            >
              Zysk
            </TableHead>
            <TableHead
              className={`
                px-4 py-4 text-right align-middle text-xs font-bold
                tracking-wider text-foreground uppercase
              `}
            >
              Wartość
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {stocks.length === 0 ? (
            <TableRow>
              <TableCell
                colSpan={6}
                className={`h-24 text-center text-muted-foreground`}
              >
                Brak danych do wyświetlenia.
              </TableCell>
            </TableRow>
          ) : (
            stocks.map((operation, index) => {
              const isProfit = operation.totalProfit > 0;

              return (
                <TableRow
                  key={operation.stockSymbol}
                  className={`
                    border-b transition-colors duration-200
                    last:border-0
                    even:bg-muted/10
                    hover:bg-primary/5
                  `}
                >
                  <TableCell className="w-full max-w-0 px-4 py-3">
                    <div className="flex items-center gap-3">
                      <div
                        className="h-8 w-1 shrink-0 rounded-full"
                        style={{
                          backgroundColor: `hsl(${(index * 137.5) % 360}, 50%, 45%)`,
                        }}
                      />
                      <div className="flex min-w-0 flex-col gap-0.5">
                        <span
                          className={`
                            block truncate leading-tight font-semibold
                            text-foreground
                          `}
                          title={operation.stockName}
                        >
                          {operation.stockName}
                        </span>
                        <span
                          className={`
                            text-[10px] font-bold tracking-tight
                            text-muted-foreground uppercase
                          `}
                        >
                          {operation.stockSymbol}
                        </span>
                      </div>
                    </div>
                  </TableCell>

                  <TableCell className="px-4 text-right font-mono text-sm">
                    {operation.totalVolume}
                  </TableCell>

                  <TableCell
                    className={`
                      px-4 text-right font-mono text-sm text-muted-foreground
                    `}
                  >
                    {formatStockPrice(operation.stockPrice, currency)}
                  </TableCell>

                  <TableCell
                    className={cn(
                      `
                        px-4 text-right font-mono text-sm font-medium
                        text-muted-foreground
                      `,
                    )}
                  >
                    {formatStockPrice(operation.averagePrice, currency)}
                  </TableCell>

                  <TableCell
                    className={cn(
                      "px-4 text-right font-mono text-sm font-bold",
                      {
                        "text-green-500": isProfit,
                        "text-red-500": !isProfit,
                      },
                    )}
                  >
                    {percentFormatter.format(operation.profitPercentage)}
                  </TableCell>
                  <TableCell
                    className={cn(
                      "px-4 text-right font-mono text-sm font-medium",
                      {
                        "text-green-500": isProfit,
                        "text-red-500": !isProfit,
                      },
                    )}
                  >
                    {formatStockPrice(operation.totalProfit, currency, {
                      fractionDigits: 2,
                    })}
                  </TableCell>
                  <TableCell
                    className={`
                      px-4 text-right font-mono text-sm font-bold
                      text-foreground
                    `}
                  >
                    {formatStockPrice(operation.totalCost, currency)}
                  </TableCell>
                </TableRow>
              );
            })
          )}
        </TableBody>
      </Table>
    </div>
  );
};
