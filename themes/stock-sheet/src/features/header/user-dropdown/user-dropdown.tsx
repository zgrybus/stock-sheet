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
            <p className="text-xl font-bold">Krzysztof Nofz</p>
            <p className="text-sm font-light text-muted">
              krzysztofnofz@gmail.com
            </p>
          </Popover.Heading>
          <Button className="w-full" variant="tertiary">
            Zarządzaj swoim kontem
          </Button>
          <Separator className="my-4" />
          <Button className="w-full flex-1 justify-start" variant="tertiary">
            <LogOut />
            Wyloguj
          </Button>
        </Popover.Dialog>
      </Popover.Content>
    </Popover>
  );
};
