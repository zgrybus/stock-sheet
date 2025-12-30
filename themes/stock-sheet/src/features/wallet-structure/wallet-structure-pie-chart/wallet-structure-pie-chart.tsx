import { Cell, Pie, PieChart } from "recharts";
import type { ChartConfig } from "@/components/ui/chart";
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart";

const config: ChartConfig = {
  totalPrice: {
    label: "Wartość",
  },
};

type Stock = {
  name: string;
  totalPrice: number;
};

type WalletPieChartProps = {
  stocks: Array<Stock>;
};

export function WalletStructurePieChart({ stocks }: WalletPieChartProps) {
  return (
    <div className="flex items-center justify-center p-6">
      <ChartContainer
        config={config}
        className="aspect-square max-h-87.5 w-full max-w-87.5"
      >
        <PieChart>
          <ChartTooltip content={<ChartTooltipContent />} />
          <Pie
            data={stocks}
            dataKey="totalPrice"
            nameKey="name"
            innerRadius={65}
            stroke="var(--card)"
            strokeWidth={2}
          >
            {stocks.map((entry) => {
              return (
                <Cell
                  key={entry.name}
                  className={`
                    cursor-pointer fill-chart-1 transition-opacity duration-200
                    outline-none
                    hover:opacity-80
                  `}
                />
              );
            })}
          </Pie>
        </PieChart>
      </ChartContainer>
    </div>
  );
}
