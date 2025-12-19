import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

type Operation = {
  id: string;
  stockValue: string;
  type: "BUY";
  volume: number;
  openDate: string;
  pricePerVolume: number;
  totalPrice: number;
};

type WalletOperationsTableProps = {
  operations: Array<Operation>;
};

export const WalletOperationsTable = ({
  operations,
}: WalletOperationsTableProps) => {
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>ID</TableHead>
          <TableHead>Data</TableHead>
          <TableHead>Nazwa spółki</TableHead>
          <TableHead>Typ</TableHead>
          <TableHead>Wolumen</TableHead>
          <TableHead>Cena za wolumen</TableHead>
          <TableHead>Cena całkowita</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {operations.map((invoice) => (
          <TableRow key={invoice.id}>
            <TableCell>{invoice.id}</TableCell>
            <TableCell>{invoice.openDate}</TableCell>
            <TableCell>{invoice.stockValue}</TableCell>
            <TableCell>{invoice.type}</TableCell>
            <TableCell>{invoice.volume}</TableCell>
            <TableCell>{invoice.pricePerVolume}</TableCell>
            <TableCell>{invoice.totalPrice}</TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};
