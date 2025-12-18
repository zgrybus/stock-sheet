import { Separator } from "@heroui/react";
import { UserDropdown } from "../user-dropdown/user-dropdown";

export const DesktopHeader = () => {
  return (
    <header>
      <div className="flex px-10 py-6">
        <div className="ml-auto">
          <UserDropdown />
        </div>
      </div>
      <Separator />
    </header>
  );
};
