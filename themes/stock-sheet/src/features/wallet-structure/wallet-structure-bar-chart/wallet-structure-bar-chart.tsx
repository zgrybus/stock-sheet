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
import { numberFormatUtil } from "@/features/number-utils/number-format-util/number-format-util";

const chartConfig = {
  totalPrice: {
    label: "Wartość",
    color: "hsl(var(--primary))",
  },
} satisfies ChartConfig;

type Stock = {
  name: string;
  totalPrice: number;
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
    () => stocks.reduce((acc, stock) => acc + stock.totalPrice, 0),
    [stocks]
  );

  const data = useMemo(() => {
    return stocks.map((stock, index) => {
      const percentVal =
        totalValue > 0 ? (stock.totalPrice / totalValue) * 100 : 0;

      const price = numberFormatUtil({
        style: "currency",
        currency,
      }).format(Number(stock.totalPrice));

      const percentage = numberFormatUtil({
        style: "percent",
      }).format(totalValue > 0 ? Number(stock.totalPrice) / totalValue : 0);

      const hue = (index * 137.5) % 360;
      const color = `hsl(${hue}, 65%, 50%)`;

      return {
        ...stock,
        price,
        percentage,
        color,
        percentVal,
      };
    });
  }, [stocks, totalValue, currency]);

  return (
    <ChartContainer config={chartConfig}>
      <BarChart
        data={data}
        layout="vertical"
        barCategoryGap={10}
        // margin={{ right: 40 }}
      >
        <XAxis type="number" hide />
        <YAxis
          dataKey="name"
          type="category"
          tickLine={false}
          axisLine={false}
          fontSize={12}
          width={100}
          fontWeight={500}
          tickFormatter={(value) => {
            const item = data.find((d) => d.name === value);
            return item ? `${item.name} (${item.price})` : value;
          }}
        />
        <Tooltip
          content={
            <ChartTooltipContent
              hideLabel
              formatter={(_value, _name, item) => {
                return (
                  <div className="flex items-center gap-2">
                    <div
                      className="size-3 rounded-xs"
                      style={{ backgroundColor: item.payload.color }}
                    />
                    <span className="font-medium">
                      <strong>{item.payload.name}</strong> ({" "}
                      {item.payload.price} ) {item.payload.percentage}
                    </span>
                  </div>
                );
              }}
            />
          }
        />
        <Bar dataKey="totalPrice" radius={[0, 4, 4, 0]}>
          <LabelList
            dataKey="percentage"
            position="insideRight"
            fontSize={13}
            fontWeight="bold"
            className="fill-foreground"
            offset={10}
          />
          {data.map((entry) => (
            <Cell key={entry.name} fill={entry.color} />
          ))}
        </Bar>
      </BarChart>
    </ChartContainer>
  );
}
