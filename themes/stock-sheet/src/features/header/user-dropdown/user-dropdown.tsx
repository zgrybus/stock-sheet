import { Avatar, Button, Popover, Separator } from "@heroui/react";
import { LogOut } from "lucide-react";

export const UserDropdown = () => {
  return (
    <Popover>
      <Popover.Trigger>
        <Avatar>
          <Avatar.Image alt="Krzysztof Nofz" />
          <Avatar.Fallback>KN</Avatar.Fallback>
        </Avatar>
      </Popover.Trigger>
      <Popover.Content className="w-64">
        <Popover.Dialog>
          <Popover.Heading className="font-bold text-xl">
            Krzysztof Nofz
          </Popover.Heading>
          <Separator className="my-4" />
          <Button className="flex-1 justify-start w-full" variant="tertiary">
            <LogOut />
            Wyloguj
          </Button>
        </Popover.Dialog>
      </Popover.Content>
    </Popover>
  );
};
