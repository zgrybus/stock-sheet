import { Button, Separator } from "@heroui/react";
import { Menu } from "lucide-react";
import { UserDropdown } from "../user-dropdown/user-dropdown";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";

export const MobileHeader = () => {
  return (
    <header>
      <div className="flex px-4 py-3">
        <Button isIconOnly variant="tertiary">
          <Menu />
        </Button>
        <img className="ml-4 h-10 w-28" srcSet={StockSheetLogo} alt="logo" />
        <div className="ml-auto">
          <UserDropdown />
        </div>
      </div>
      <Separator />
    </header>
  );
};
