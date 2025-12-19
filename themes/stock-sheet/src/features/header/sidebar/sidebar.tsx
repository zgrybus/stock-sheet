import { IdCard, Wallet } from "lucide-react";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";
import { Separator } from "@/components/ui/separator";

export const Sidebar = () => {
  return (
    <>
      <div className="flex flex-1 flex-col items-center gap-6 px-2 py-4">
        <img className="h-10 w-28" srcSet={StockSheetLogo} alt="logo" />
        <Accordion type="multiple">
          <AccordionItem value="wallet">
            <AccordionTrigger>
              <div className="flex items-center text-lg">
                <Wallet className="mr-3 shrink-0 text-primary" />
                Portfel
              </div>
            </AccordionTrigger>
            <AccordionContent>Struktura</AccordionContent>
          </AccordionItem>
          <AccordionItem value="operation">
            <AccordionTrigger>
              <div className="flex items-center text-lg">
                <IdCard className="mr-3 shrink-0 text-primary" />
                Operacje
              </div>
            </AccordionTrigger>
            <AccordionContent>Import operacji</AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>
      <Separator orientation="vertical" />
    </>
  );
};
