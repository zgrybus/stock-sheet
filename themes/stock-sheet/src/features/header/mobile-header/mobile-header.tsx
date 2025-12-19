import { Menu } from "lucide-react";
import { UserDropdown } from "../user-dropdown/user-dropdown";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";
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
            <Button variant="default" size="icon">
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

        <img className="h-6 w-auto" srcSet={StockSheetLogo} alt="logo" />
      </div>
      <UserDropdown />
    </header>
  );
};
