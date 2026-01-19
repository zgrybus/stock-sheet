import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { numberFormatUtil } from "@/features/number-utils/number-format-util/number-format-util";

type Operation = {
  name: string;
  volumes: number;
  averagePrice: number;
  totalPrice: number;
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
    <div className="overflow-hidden rounded-md border bg-card">
      <Table>
        <TableHeader className="bg-secondary">
          <TableRow className="hover:bg-secondary">
            <TableHead
              className={`
                w-full text-xs font-bold tracking-wider text-foreground
                uppercase
              `}
            >
              Walor
            </TableHead>
            <TableHead
              className={`
                px-8 text-right text-xs font-bold tracking-wider
                whitespace-nowrap text-foreground uppercase
              `}
            >
              Wolumeny
            </TableHead>
            <TableHead
              className={`
                px-8 text-right text-xs font-bold tracking-wider
                whitespace-nowrap text-foreground uppercase
              `}
            >
              Średnia cena zakupu
            </TableHead>
            <TableHead
              className={`
                px-8 text-right text-xs font-bold tracking-wider
                whitespace-nowrap text-foreground uppercase
              `}
            >
              Całkowita wartość
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {stocks.length === 0 ? (
            <TableRow>
              <TableCell colSpan={4} className="h-24 text-center">
                Brak danych do wyświetlenia.
              </TableCell>
            </TableRow>
          ) : (
            stocks.map((operation) => (
              <TableRow
                key={operation.name}
                className={`
                  border-b-0
                  even:bg-muted/50
                  hover:bg-muted/60
                `}
              >
                <TableCell className="py-3 font-semibold text-foreground">
                  {operation.name}
                </TableCell>
                <TableCell className="px-8 text-right font-mono">
                  {operation.volumes}
                </TableCell>
                <TableCell
                  className={`px-8 text-right font-mono text-muted-foreground`}
                >
                  {numberFormatUtil({
                    style: "currency",
                    currency,
                  }).format(operation.averagePrice)}
                </TableCell>
                <TableCell
                  className={`
                    px-8 text-right font-mono font-bold text-foreground
                  `}
                >
                  {numberFormatUtil({
                    style: "currency",
                    currency,
                  }).format(operation.totalPrice)}
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  );
};
