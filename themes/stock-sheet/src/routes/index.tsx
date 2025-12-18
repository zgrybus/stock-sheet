import { Button } from "@heroui/react";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/")({
  component: Index,
});

function Index() {
  return (
    <div>
      Sphinx of black quartz, judge my vow.
      <Button>Save</Button>
    </div>
  );
}
