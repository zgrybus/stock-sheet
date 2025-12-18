import { Separator } from "@heroui/react";
import { Sidebar } from "../sidebar/sidebar";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";

export const DesktopSidebar = () => {
  return (
    <aside className="w-60 min-h-dvh flex">
      <div className="px-2 py-4 flex flex-col flex-1 items-center gap-6">
        <img className="w-28 h-10" srcSet={StockSheetLogo} alt="logo" />
        <Sidebar />
      </div>
      <Separator orientation="vertical" />
    </aside>
  );
};
