import { Menu } from "lucide-react";
import { UserDropdown } from "../user-dropdown/user-dropdown";
import { Button } from "@/components/ui/button";
import {
  Drawer,
  DrawerContent,
  DrawerDescription,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { Sidebar } from "@/features/header/sidebar/sidebar";
import { PortfolioSelector } from "../portfolio-selector/portfolio-selector";

export const MobileHeader = () => {
  return (
    <header
      className={`
        sticky top-0 z-50 flex h-14 w-full items-center justify-between border-b
        border-border bg-background/80 px-4 backdrop-blur-md
      `}
    >
      <div className="flex items-center gap-3">
        <Drawer direction="left">
          <DrawerTrigger asChild>
            <Button
              variant="default"
              size="icon"
              aria-label="Otwórz menu mobilne"
            >
              <Menu />
            </Button>
          </DrawerTrigger>
          <DrawerContent>
            <DrawerHeader className="sr-only">
              <DrawerTitle>Menu</DrawerTitle>
              <DrawerDescription>Nawigacja główna</DrawerDescription>
            </DrawerHeader>
            <Sidebar />
          </DrawerContent>
        </Drawer>
        <PortfolioSelector />
      </div>
      <UserDropdown />
    </header>
  );
};
