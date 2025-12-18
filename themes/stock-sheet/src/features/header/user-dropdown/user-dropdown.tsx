import { Avatar, Popover } from "@heroui/react";

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
          <Popover.Heading>Krzysztof Nofz</Popover.Heading>
        </Popover.Dialog>
      </Popover.Content>
    </Popover>
  );
};
