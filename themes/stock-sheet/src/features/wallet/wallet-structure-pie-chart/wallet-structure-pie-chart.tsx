import { Cell, Pie, PieChart } from "recharts";
import type { PieLabelRenderProps } from "recharts";
import type { ChartConfig } from "@/components/ui/chart";
import {
  ChartContainer,
  ChartLegend,
  ChartLegendContent,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart";
import { useMemo } from "react";
import {
  formatStockPrice,
  numberFormatUtil,
} from "@/features/number-utils/number-format-util/number-format-util";

type Stock = {
  totalCost: number;
  stockName: string;
  stockSymbol: string;
};

type WalletPieChartProps = {
  currency: string;
  stocks: Array<Stock>;
};

export function WalletStructurePieChart({
  currency,
  stocks,
}: WalletPieChartProps) {
  const chartConfig = useMemo(() => {
    return stocks.reduce(
      (acc, stock, index) => {
        const hue = (index * 137.5) % 360;

        acc[stock.stockSymbol] = {
          label: stock.stockName,
          color: `hsl(${hue}, 50%, 45%)`,
        };
        return acc;
      },
      { totalPrice: { label: "Wartość" } } as ChartConfig,
    );
  }, [stocks]);

  const totalValue = useMemo(
    () => stocks.reduce((acc, stock) => acc + stock.totalCost, 0),
    [stocks],
  );

  const data = useMemo(() => {
    return stocks.map((stock, index) => {
      const price = formatStockPrice(stock.totalCost, currency);

      const percentage = numberFormatUtil({
        style: "percent",
        maximumFractionDigits: 2,
        minimumFractionDigits: 2,
      }).format(totalValue > 0 ? Number(stock.totalCost) / totalValue : 0);

      const hue = (index * 137.5) % 360;
      const color = `hsl(${hue}, 50%, 45%)`;

      return {
        ...stock,
        price,
        percentage,
        color,
      };
    });
  }, [stocks, totalValue, currency]);

  return (
    <div className="flex items-center justify-center p-6">
      <ChartContainer
        config={chartConfig}
        className="aspect-square max-h-146 w-full max-w-full"
      >
        <PieChart>
          <ChartTooltip
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
          <Pie
            data={data}
            style={{ stroke: "var(--card)" }}
            dataKey="totalCost"
            nameKey="stockSymbol"
            innerRadius={80}
            strokeWidth={3}
            labelLine={false}
            label={renderCustomizedLabel}
          >
            {stocks.map((entry) => (
              <Cell
                key={entry.stockSymbol}
                fill={chartConfig[entry.stockSymbol].color}
                className={`
                  cursor-pointer transition-opacity duration-200 outline-none
                  hover:opacity-80
                `}
              />
            ))}
          </Pie>
          <ChartLegend
            content={
              <ChartLegendContent
                nameKey="stockSymbol"
                className={`flex-wrap justify-center gap-x-4 gap-y-2`}
              />
            }
          />
        </PieChart>
      </ChartContainer>
    </div>
  );
}

const RADIAN = Math.PI / 180;

const renderCustomizedLabel = ({
  cx,
  cy,
  midAngle,
  innerRadius,
  outerRadius,
  percentage,
  percent,
}: PieLabelRenderProps) => {
  if (typeof percent === "number" && percent * 100 < 5) {
    return null;
  }

  if (cx == null || cy == null || innerRadius == null || outerRadius == null) {
    return null;
  }

  const radius =
    Number(innerRadius) + (Number(outerRadius) - Number(innerRadius)) * 0.5;
  const ncx = Number(cx);
  const ncy = Number(cy);

  const x = ncx + radius * Math.cos(-(midAngle ?? 0) * RADIAN);
  const y = ncy + radius * Math.sin(-(midAngle ?? 0) * RADIAN);

  return (
    <text
      x={x}
      y={y}
      fill="white"
      textAnchor="middle"
      dominantBaseline="central"
      className={`
        pointer-events-none text-[13px] font-bold
        drop-shadow-[0_1px_2px_rgba(0,0,0,0.5)] select-none
      `}
    >
      {percentage}
    </text>
  );
};
