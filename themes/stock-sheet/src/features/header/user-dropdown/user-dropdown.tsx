import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { LogOut, Settings } from "lucide-react";

export const UserDropdown = () => {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <Avatar className="size-10 border-2 border-border">
          <AvatarImage alt="Krzysztof Nofz" />
          <AvatarFallback>KN</AvatarFallback>
        </Avatar>
      </PopoverTrigger>
      <PopoverContent className="p-4" align="end">
        <div className="flex flex-col gap-2 p-2 text-left">
          <p
            className={`
              text-lg leading-none font-medium
              lg:text-xl
            `}
          >
            Krzysztof Nofz
          </p>
          <p
            className={`
              text-sm leading-none text-muted-foreground
              lg:text-base
            `}
          >
            krzysztofnofz@gmail.com
          </p>
        </div>
        <Button variant="outline" className="mt-4 w-full">
          <Settings />
          Zarządzaj kontem
        </Button>
        <Separator className="my-3" />
        <Button
          className={`
            w-full justify-start text-destructive
            hover:bg-destructive/10 hover:text-destructive
          `}
          variant="ghost"
        >
          <LogOut className="mr-2 h-3.5 w-3.5" />
          Wyloguj
        </Button>
      </PopoverContent>
    </Popover>
  );
};
