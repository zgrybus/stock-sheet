import { PortfolioSelector } from "../portfolio-selector/portfolio-selector";
import { UserDropdown } from "../user-dropdown/user-dropdown";

export const DesktopHeader = () => {
  return (
    <header
      className={`
        sticky top-0 z-50 flex h-14 w-full items-center justify-between border-b
        border-border bg-background/80 px-6 backdrop-blur-md
      `}
    >
      <PortfolioSelector />
      <UserDropdown />
    </header>
  );
};
