import { IdCard, Wallet } from "lucide-react";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";
import { Link } from "@tanstack/react-router";

export const Sidebar = () => {
  return (
    <div className="flex h-full w-64 flex-col">
      <div className="px-6 pt-8 pb-6">
        <Link
          to="/"
          aria-label="Przekieruj na stronę główną"
          className="block"
          search
        >
          <img
            className="h-7 w-auto"
            srcSet={StockSheetLogo}
            alt="Stock Sheet"
          />
        </Link>
      </div>

      <div className="flex-1 px-3">
        <Accordion type="single">
          <AccordionItem value="wallet">
            <AccordionTrigger
              className={`
                mb-4 rounded-md px-3 py-2 text-base font-medium
                hover:bg-muted/50 hover:text-foreground hover:no-underline
              `}
            >
              <div className="flex items-center gap-3">
                <Wallet className="size-4 text-primary" />
                <span>Portfel</span>
              </div>
            </AccordionTrigger>
            <AccordionContent className="ml-5">
              <Link
                to="/wallet/dashboard"
                className={`
                  block rounded-md px-3 py-2 text-sm text-muted-foreground
                  transition-colors
                  hover:bg-muted/50 hover:text-foreground
                `}
                activeProps={{
                  className: "bg-accent text-accent-foreground font-semibold",
                }}
                search
              >
                Pulpit
              </Link>
              <Link
                to="/wallet/structure"
                className={`
                  block rounded-md px-3 py-2 text-sm text-muted-foreground
                  transition-colors
                  hover:bg-muted/50 hover:text-foreground
                `}
                activeProps={{
                  className: "bg-accent text-accent-foreground font-semibold",
                }}
                search
              >
                Struktura
              </Link>
            </AccordionContent>
          </AccordionItem>

          <AccordionItem value="operation" className="border-none">
            <AccordionTrigger
              className={`
                mb-4 rounded-md px-3 py-2 text-base font-medium
                hover:bg-muted/50 hover:text-foreground hover:no-underline
              `}
            >
              <div className="flex items-center gap-3">
                <IdCard className="size-4 text-primary" />
                <span>Operacje</span>
              </div>
            </AccordionTrigger>
            <AccordionContent
              className={`
                ml-5 space-y-1 border-l border-muted/50 pt-1 pb-1 pl-4
              `}
            >
              <Link
                to="/operations/import"
                className={`
                  block rounded-md px-3 py-2 text-sm text-muted-foreground
                  transition-colors
                  hover:bg-muted/50 hover:text-foreground
                `}
                activeProps={{
                  className: "bg-accent text-accent-foreground font-semibold",
                }}
                search
              >
                Import operacji
              </Link>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      </div>
    </div>
  );
};
