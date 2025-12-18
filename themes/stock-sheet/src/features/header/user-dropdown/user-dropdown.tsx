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
          <Popover.Heading className="mb-6">
            <p className="font-bold text-xl">Krzysztof Nofz</p>
            <p className="text-muted text-sm font-light">
              krzysztofnofz@gmail.com
            </p>
          </Popover.Heading>
          <Button className="w-full" variant="tertiary">
            Zarządzaj swoim kontem
          </Button>
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
