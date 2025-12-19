import { Separator } from "@/components/ui/separator";
import { Sidebar } from "../sidebar/sidebar";
import StockSheetLogo from "@/features/assets/stock-sheet-logo.png?w=112;224&as=srcset&imagetools";

export const DesktopSidebar = () => {
  return (
    <aside className="flex min-h-dvh w-60">
      <div className="flex flex-1 flex-col items-center gap-6 px-2 py-4">
        <img className="h-10 w-28" srcSet={StockSheetLogo} alt="logo" />
        <Sidebar />
      </div>
      <Separator orientation="vertical" />
    </aside>
  );
};
