import { useMemo } from "react";
import {
  Bar,
  BarChart,
  Cell,
  LabelList,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { ChartContainer, ChartTooltipContent } from "@/components/ui/chart";
import type { ChartConfig } from "@/components/ui/chart";
import {
  formatStockPrice,
  numberFormatUtil,
} from "@/features/number-utils/number-format-util/number-format-util";

const chartConfig = {
  totalPrice: {
    label: "Wartość",
    color: "hsl(var(--primary))",
  },
} satisfies ChartConfig;

type Stock = {
  totalCost: number;
  stockSymbol: string;
  stockName: string;
};

type WalletBarChartProps = {
  currency: string;
  stocks: Array<Stock>;
};

export function WalletStructureBarChart({
  currency,
  stocks,
}: WalletBarChartProps) {
  const totalValue = useMemo(
    () => stocks.reduce((acc, stock) => acc + stock.totalCost, 0),
    [stocks],
  );

  const data = useMemo(() => {
    return stocks.map((stock, index) => {
      const price = formatStockPrice(stock.totalCost, currency);

      const percentage = numberFormatUtil({
        maximumFractionDigits: 2,
        minimumFractionDigits: 2,
        style: "percent",
      }).format(totalValue > 0 ? Number(stock.totalCost) / totalValue : 0);

      const hue = (index * 137.5) % 360;
      const color = `hsl(${hue}, 50%, 40%)`;

      return {
        ...stock,
        price,
        percentage,
        color,
      };
    });
  }, [stocks, totalValue, currency]);

  const barChartContainerHeight = Math.max(stocks.length * 50, 50);

  return (
    <div style={{ height: barChartContainerHeight }} className="w-full pr-12">
      <ChartContainer config={chartConfig} className="h-full w-full">
        <BarChart
          data={data}
          layout="vertical"
          barSize={20}
          margin={{ top: 0, right: 0, bottom: 0, left: 10 }}
        >
          <XAxis type="number" hide />
          <YAxis
            dataKey="stockSymbol"
            type="category"
            tickLine={false}
            axisLine={false}
            width={140}
            tick={({ x, y, payload }) => {
              const item = data.find((d) => d.stockSymbol === payload.value)!;

              return (
                <foreignObject x={x - 140} y={y - 22} width={132} height={50}>
                  <div
                    className={`
                      flex h-full w-full flex-col justify-center text-right
                      text-xs leading-4
                    `}
                  >
                    <span
                      className="line-clamp-2 font-semibold text-foreground"
                      title={item.stockName}
                    >
                      {item.stockName}
                    </span>
                    <span
                      className={`
                        mt-0.5 font-normal whitespace-nowrap
                        text-muted-foreground
                      `}
                    >
                      ({item.price})
                    </span>
                  </div>
                </foreignObject>
              );
            }}
          />
          <Tooltip
            content={
              <ChartTooltipContent
                hideLabel
                formatter={(_value, _name, item) => {
                  return (
                    <div className="flex items-center gap-2 font-medium">
                      <div
                        className="size-3 rounded-xs"
                        style={{ backgroundColor: item.payload.color }}
                      />
                      <span>
                        <strong>{item.payload.stockName}</strong> ({" "}
                        {item.payload.price} ) {item.payload.percentage}
                      </span>
                    </div>
                  );
                }}
              />
            }
          />
          <Bar dataKey="totalCost" radius={[0, 4, 4, 0]}>
            <LabelList
              dataKey="percentage"
              position="right"
              fontSize={12}
              fontWeight="500"
              className="fill-muted-foreground"
              offset={12}
            />
            {data.map((entry) => (
              <Cell key={entry.stockSymbol} fill={entry.color} />
            ))}
          </Bar>
        </BarChart>
      </ChartContainer>
    </div>
  );
}
