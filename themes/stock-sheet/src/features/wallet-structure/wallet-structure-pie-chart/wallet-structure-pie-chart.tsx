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
import { numberFormatUtil } from "@/features/number-utils/number-format-util/number-format-util";

type Stock = {
  name: string;
  totalPrice: number;
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

        acc[stock.name] = {
          label: stock.name,
          color: `hsl(${hue}, 70%, 45%)`,
        };
        return acc;
      },
      { totalPrice: { label: "Wartość" } } as ChartConfig
    );
  }, [stocks]);

  const totalValue = useMemo(
    () => stocks.reduce((acc, stock) => acc + stock.totalPrice, 0),
    [stocks]
  );

  return (
    <div className="flex items-center justify-center p-6">
      <ChartContainer
        config={chartConfig}
        className="aspect-square max-h-140 w-full max-w-140"
      >
        <PieChart>
          <ChartTooltip
            content={
              <ChartTooltipContent
                hideLabel
                formatter={(value, name, item) => {
                  const price = numberFormatUtil({
                    style: "currency",
                    currency,
                  }).format(Number(value));

                  const percentage = numberFormatUtil({
                    style: "percent",
                  }).format(totalValue > 0 ? Number(value) / totalValue : 0);

                  return (
                    <div className="flex items-center gap-2 font-medium">
                      <div
                        className="size-3 rounded-xs"
                        style={{ backgroundColor: item.payload.fill }}
                      />
                      <span>
                        <strong>{name}</strong> ( {price} ) {percentage}
                      </span>
                    </div>
                  );
                }}
              />
            }
          />
          <Pie
            data={stocks}
            dataKey="totalPrice"
            nameKey="name"
            innerRadius={80}
            strokeWidth={5}
            labelLine={false}
            label={renderCustomizedLabel}
          >
            {stocks.map((entry) => (
              <Cell
                key={entry.name}
                fill={chartConfig[entry.name].color}
                className={`
                  cursor-pointer transition-opacity duration-200 outline-none
                  hover:opacity-80
                `}
              />
            ))}
          </Pie>
          <ChartLegend content={<ChartLegendContent nameKey="name" />} />
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
  percent,
}: PieLabelRenderProps) => {
  if (cx == null || cy == null || innerRadius == null || outerRadius == null) {
    return null;
  }

  // Obliczamy promień tak, aby tekst był idealnie na środku "mięsa" pierścienia
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
      {`${((percent ?? 0) * 100).toFixed(0)}%`}
    </text>
  );
};

// const RADIAN = Math.PI / 180;

// const renderCustomizedLabel = ({
//   cx,
//   cy,
//   midAngle,
//   innerRadius,
//   outerRadius,
//   percent,
// }: PieLabelRenderProps) => {
//   if (cx == null || cy == null || innerRadius == null || outerRadius == null) {
//     return null;
//   }
//   const radius =
//     Number(innerRadius) + (Number(outerRadius) - Number(innerRadius)) * 0.5;
//   const ncx = Number(cx);
//   const x = ncx + radius * Math.cos(-(midAngle ?? 0) * RADIAN);
//   const ncy = Number(cy);
//   const y = ncy + radius * Math.sin(-(midAngle ?? 0) * RADIAN);

//   return (
//     <text
//       x={x}
//       y={y}
//       fill="white"
//       textAnchor={x > ncx ? "start" : "end"}
//       dominantBaseline="central"
//     >
//       {`${((percent ?? 1) * 100).toFixed(0)}%`}
//     </text>
//   );
// };
