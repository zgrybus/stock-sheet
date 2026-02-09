import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverTrigger,
  PopoverContent,
} from "@/components/ui/popover";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";
import { PlusCircle, Wallet, ChevronDown } from "lucide-react";

const MOCK_PORTFOLIOS = [
  { id: "1", name: "Portfel REIT" },
  { id: "2", name: "Portfel Polski" },
  { id: "3", name: "Akcje USA" },
];

export const PortfolioSelector = () => {
  const currentPortfolioId = "1";
  const currentPortfolio = MOCK_PORTFOLIOS.find(
    (p) => p.id === currentPortfolioId,
  );

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button variant="ghost" className="group">
          <Wallet className="mr-2 size-4 text-primary" />
          <span className="text-base text-foreground">
            {currentPortfolio?.name || "Wybierz portfel"}
          </span>
          <ChevronDown
            className={`
              size-4 text-muted-foreground transition-transform duration-200
              group-data-[state=open]:rotate-180
            `}
          />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-64 p-2" align="start">
        <div
          className={`
            px-2 pt-2 pb-1 text-xs font-semibold tracking-wider
            text-muted-foreground uppercase
          `}
        >
          Moje Portfele
        </div>
        <div className="mt-3 flex flex-col gap-1">
          {MOCK_PORTFOLIOS.map((portfolio) => {
            const isActive = portfolio.id === currentPortfolioId;
            return (
              <Button
                key={portfolio.id}
                variant="ghost"
                className="w-full justify-between px-2 font-normal"
              >
                <div className="flex items-center">
                  <Wallet
                    className={cn("mr-2 size-4 opacity-30", {
                      "text-primary opacity-100": isActive,
                    })}
                  />
                  <span
                    className={cn({
                      "text-primary": isActive,
                    })}
                  >
                    {portfolio.name}
                  </span>
                </div>
              </Button>
            );
          })}
        </div>
        <Separator className="my-2" />
        <Button
          variant="ghost"
          className={`
            w-full justify-start text-primary
            hover:text-primary
          `}
        >
          <PlusCircle className="mr-2 size-4" />
          Dodaj nowy portfel
        </Button>
      </PopoverContent>
    </Popover>
  );
};
