import { Separator } from "@heroui/react";
import { UserDropdown } from "../user-dropdown/user-dropdown";

export const DesktopHeader = () => {
  return (
    <header>
      <div className="py-6 px-10 flex">
        <div className="ml-auto">
          <UserDropdown />
        </div>
      </div>
      <Separator />
    </header>
  );
};
