import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { priceFormatUtil } from "@/features/number-utils/number-format-util/number-format-util";
import { parse, isValid, format } from "date-fns";

type Operation = {
  id: string;
  stockValue: string;
  type: "BUY" | "SELL";
  volume: number;
  openDate: string;
  pricePerVolume: number;
  totalPrice: number;
};

type WalletOperationsTableProps = {
  operations: Array<Operation>;
};

const formatDate = (dateString: string) => {
  const parsedDate = parse(dateString, "dd/MM/yyyy HH:mm:ss", new Date());

  if (!isValid(parsedDate)) {
    return "-";
  }

  return format(parsedDate, "dd.MM.yyyy, HH:mm");
};

export const WalletOperationsTable = ({
  operations,
}: WalletOperationsTableProps) => {
  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow className="bg-muted/50">
            <TableHead className="w-25">ID</TableHead>
            <TableHead>Data</TableHead>
            <TableHead>Instrument</TableHead>
            <TableHead>Typ</TableHead>
            <TableHead className="text-right">Wolumen</TableHead>
            <TableHead className="text-right">Cena (jedn.)</TableHead>
            <TableHead className="text-right">Wartość</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {operations.map((operation) => (
            <TableRow key={operation.id} className="hover:bg-muted/50">
              <TableCell className="font-mono text-xs text-muted-foreground">
                {operation.id}
              </TableCell>
              <TableCell className="text-sm whitespace-nowrap">
                {formatDate(operation.openDate)}
              </TableCell>
              <TableCell className="font-medium">
                {operation.stockValue}
              </TableCell>
              <TableCell>
                <Badge
                  variant="outline"
                  className={`
                    ${
                      operation.type === "BUY"
                        ? "border-green-500/30 bg-green-500/10 text-green-500"
                        : "border-red-500/30 bg-red-500/10 text-red-500"
                    }
                  `}
                >
                  {operation.type}
                </Badge>
              </TableCell>
              <TableCell className="text-right">{operation.volume}</TableCell>
              <TableCell className="text-right text-muted-foreground">
                {priceFormatUtil.format(operation.pricePerVolume)}
              </TableCell>
              <TableCell className="text-right font-semibold">
                {priceFormatUtil.format(operation.totalPrice)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};
