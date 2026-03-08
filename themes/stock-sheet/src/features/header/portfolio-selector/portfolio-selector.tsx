import { $apiStockSheet } from "@/apis/stock-sheet/client";
import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverTrigger,
  PopoverContent,
} from "@/components/ui/popover";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";
import { Link, useSearch } from "@tanstack/react-router";
import {
  PlusCircle,
  Wallet,
  ChevronDown,
  AlertCircle,
  Inbox,
  X,
} from "lucide-react";
import { useState } from "react";
import { match, P } from "ts-pattern";

export const PortfolioSelector = () => {
  const selectedPortfolioId = useSearch({
    from: "__root__",
    select: (search) => search.portfolioId,
  });

  const [isOpen, setOpen] = useState(false);

  const portfolioQuery = $apiStockSheet.useQuery("get", "/api/portfolio/list");
  const { data = [] } = portfolioQuery;

  const selectedPortfolio = data.find(
    (portfolio) => selectedPortfolioId === portfolio.id,
  );

  return (
    <Popover open={isOpen} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="ghost"
          className="group flex w-50 items-center justify-start px-3"
          title={selectedPortfolio?.name}
        >
          <Wallet className="mr-2 size-4 shrink-0 text-primary" />
          <span className="flex-1 truncate text-left text-base text-foreground">
            {selectedPortfolio?.name || "Wybierz portfel"}
          </span>
          <ChevronDown
            className={`
              ml-2 size-4 shrink-0 text-muted-foreground transition-transform
              duration-200
              group-data-[state=open]:rotate-180
            `}
          />
        </Button>
      </PopoverTrigger>
      <PopoverContent
        className="w-64 p-2"
        align="start"
        aria-label="Wybór portfela"
      >
        <div
          className={`
            px-2 pt-2 pb-1 text-xs font-semibold tracking-wider
            text-muted-foreground uppercase
          `}
        >
          Moje Portfele
        </div>
        <div className="mt-3 flex flex-col gap-1">
          {match(portfolioQuery)
            .with({ isPending: true }, () => (
              <>
                {[1, 2, 3].map((i) => (
                  <div key={i} className="flex h-9 w-full items-center px-2">
                    <div
                      className={`
                        mr-2 size-4 animate-pulse rounded-full bg-muted
                      `}
                    />
                    <div className="h-4 w-24 animate-pulse rounded bg-muted" />
                  </div>
                ))}
              </>
            ))
            .with({ isError: true }, () => (
              <div
                className={`
                  flex flex-col items-center gap-2 px-2 py-4 text-center text-xs
                  text-destructive
                `}
              >
                <AlertCircle className="size-4" />
                <span>Nie udało się pobrać danych</span>
              </div>
            ))
            .with({ data: P.when((d) => d.length === 0) }, () => (
              <div
                className={`
                  flex flex-col items-center gap-2 px-2 py-4 text-center text-xs
                  text-muted-foreground
                `}
              >
                <Inbox className="size-4 opacity-20" />
                <span>Brak portfeli</span>
              </div>
            ))
            .with({ data: P.select() }, (portfolios) =>
              portfolios.map((portfolio) => {
                const isActive = portfolio.id === selectedPortfolioId;
                return (
                  <div
                    key={portfolio.id}
                    className={`group relative flex w-full items-center`}
                  >
                    <Button
                      asChild
                      variant="ghost"
                      className="w-full justify-start pr-8 pl-2 font-normal"
                      onClick={() => setOpen(false)}
                    >
                      <Link
                        to="."
                        search={(prev) => ({
                          ...prev,
                          portfolioId: portfolio.id,
                        })}
                      >
                        <div
                          className={`flex w-full items-center overflow-hidden`}
                        >
                          <Wallet
                            className={cn(
                              "mr-2 size-4 shrink-0 opacity-30",
                              isActive && "text-primary opacity-100",
                            )}
                          />
                          <span
                            className={cn(
                              "truncate",
                              isActive && "text-primary",
                            )}
                          >
                            {portfolio.name}
                          </span>
                        </div>
                      </Link>
                    </Button>
                    <Button
                      size={"icon-xs"}
                      variant={"destructive"}
                      className={`
                        pointer-events-none absolute top-1/2 right-2
                        -translate-y-1/2 opacity-0 transition-opacity
                        group-hover:pointer-events-auto group-hover:opacity-100
                        focus-visible:pointer-events-auto
                        focus-visible:opacity-100
                      `}
                      asChild
                    >
                      <Link
                        to="."
                        search={(prev) => ({
                          ...prev,
                          deletePortfolioId: portfolio.id,
                        })}
                      >
                        <X />
                      </Link>
                    </Button>
                  </div>
                );
              }),
            )
            .exhaustive()}
        </div>
        <Separator className="my-2" />
        <Button
          asChild
          variant="ghost"
          className={`
            w-full justify-start text-primary
            hover:text-primary
          `}
          onClick={() => setOpen(false)}
        >
          <Link to="/create-portfolio" search>
            <PlusCircle className="mr-2 size-4" />
            Dodaj nowy portfel
          </Link>
        </Button>
      </PopoverContent>
    </Popover>
  );
};
