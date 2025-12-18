import { Accordion } from "@heroui/react";
import { ChevronDown, IdCard, Wallet } from "lucide-react";

export const Sidebar = () => {
  return (
    <Accordion>
      <Accordion.Item>
        <Accordion.Heading>
          <Accordion.Trigger>
            <Wallet className="text-muted mr-3 size-4 shrink-0" />
            Portfel
            <Accordion.Indicator>
              <ChevronDown />
            </Accordion.Indicator>
          </Accordion.Trigger>
        </Accordion.Heading>
        <Accordion.Panel>
          <Accordion.Body>Sklad</Accordion.Body>
        </Accordion.Panel>
      </Accordion.Item>
      <Accordion.Item>
        <Accordion.Heading>
          <Accordion.Trigger>
            <IdCard className="text-muted mr-3 size-4 shrink-0" />
            Operacje
            <Accordion.Indicator>
              <ChevronDown />
            </Accordion.Indicator>
          </Accordion.Trigger>
        </Accordion.Heading>
        <Accordion.Panel>
          <Accordion.Body>Import operacji</Accordion.Body>
        </Accordion.Panel>
      </Accordion.Item>
    </Accordion>
  );
};
