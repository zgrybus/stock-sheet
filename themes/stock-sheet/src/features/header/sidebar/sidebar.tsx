import { IdCard, Wallet } from "lucide-react";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";

export const Sidebar = () => {
  return (
    <div className="flex h-full flex-col gap-4 p-4">
      <div className="px-2 pt-1 pb-2">
        <img className="h-8 w-auto" srcSet={StockSheetLogo} alt="Stock Sheet" />
      </div>
      <Accordion type="multiple" className="w-full space-y-1">
        <AccordionItem value="wallet">
          <AccordionTrigger>
            <div className="flex items-center gap-3">
              <Wallet className="h-4 w-4 text-primary" />
              <span>Portfel</span>
            </div>
          </AccordionTrigger>
          <AccordionContent>
            <span>Struktura</span>
            <span>Analiza Zysków</span>
            <span>Dywidendy</span>
          </AccordionContent>
        </AccordionItem>

        <AccordionItem value="operation">
          <AccordionTrigger>
            <div className="flex items-center gap-3">
              <IdCard className="h-4 w-4 text-primary" />
              <span>Operacje</span>
            </div>
          </AccordionTrigger>
          <AccordionContent>
            <span>Import operacji</span>
            <span>Historia transakcji</span>
            <span>Gotówka</span>
          </AccordionContent>
        </AccordionItem>
      </Accordion>
    </div>
  );
};
