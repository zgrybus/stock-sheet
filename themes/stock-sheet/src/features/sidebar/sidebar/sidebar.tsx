import { IdCard, Wallet } from "lucide-react";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";

export const Sidebar = () => {
  return (
    <Accordion type="multiple">
      <AccordionItem value="wallet">
        <AccordionTrigger>
          <Wallet className="mr-3 size-4 shrink-0 text-muted" />
          Portfel
        </AccordionTrigger>
        <AccordionContent>Struktura</AccordionContent>
      </AccordionItem>
      <AccordionItem value="operation">
        <AccordionTrigger>
          <IdCard className="mr-3 size-4 shrink-0 text-muted" />
          Operacje
        </AccordionTrigger>
        <AccordionContent>Import operacji</AccordionContent>
      </AccordionItem>
    </Accordion>
  );
};
