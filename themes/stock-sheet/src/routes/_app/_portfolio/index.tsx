import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_app/_portfolio/")({
  component: Index,
});

function Index() {
  return (
    <div>
      <div>
        <h2 className="text-2xl font-bold tracking-tight">Pulpit</h2>
      </div>
    </div>
  );
}
