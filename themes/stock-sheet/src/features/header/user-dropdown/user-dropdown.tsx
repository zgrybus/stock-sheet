import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { LogOut } from "lucide-react";

export const UserDropdown = () => {
  return (
    <Popover>
      <PopoverTrigger>
        <Avatar>
          <AvatarImage alt="Krzysztof Nofz" />
          <AvatarFallback>KN</AvatarFallback>
        </Avatar>
      </PopoverTrigger>
      <PopoverContent className="w-64">
        <p className="text-xl font-bold">Krzysztof Nofz</p>
        <p className="text-sm font-light text-muted">krzysztofnofz@gmail.com</p>
        <Button className="w-full">Zarządzaj swoim kontem</Button>
        <Separator className="my-4" />
        <Button>
          <LogOut />
          Wyloguj
        </Button>
      </PopoverContent>
    </Popover>
  );
};
