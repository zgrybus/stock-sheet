import { Button, Separator } from "@heroui/react";
import { Menu } from "lucide-react";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";
import { UserDropdown } from "../user-dropdown/user-dropdown";

export const MobileHeader = () => {
  return (
    <header>
      <div className="py-3 px-4 flex">
        <Button isIconOnly variant="tertiary">
          <Menu />
        </Button>
        <img className="w-28 h-10 ml-4" srcSet={StockSheetLogo} alt="logo" />
        <div className="ml-auto">
          <UserDropdown />
        </div>
      </div>
      <Separator />
    </header>
  );
};
