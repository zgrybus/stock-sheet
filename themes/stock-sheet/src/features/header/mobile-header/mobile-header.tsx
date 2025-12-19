import { Menu } from "lucide-react";
import { UserDropdown } from "../user-dropdown/user-dropdown";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";
import { Separator } from "@/components/ui/separator";
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
    <header>
      <div className="flex items-center px-4 py-3">
        <Drawer direction="left">
          <DrawerTrigger asChild>
            <Button>
              <Menu />
            </Button>
          </DrawerTrigger>
          <DrawerContent>
            <DrawerHeader className="sr-only">
              <DrawerTitle>Menu nawigacyjne</DrawerTitle>
              <DrawerDescription>
                Główne menu nawigacji aplikacji.
              </DrawerDescription>
            </DrawerHeader>
            <Sidebar />
          </DrawerContent>
        </Drawer>
        <img className="ml-4 h-10 w-28" srcSet={StockSheetLogo} alt="logo" />
        <div className="ml-auto">
          <UserDropdown />
        </div>
      </div>
      <Separator />
    </header>
  );
};
