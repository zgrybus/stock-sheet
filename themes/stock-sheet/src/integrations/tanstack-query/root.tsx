import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

// eslint-disable-next-line react-refresh/only-export-components
export function getContext() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { staleTime: 180000 } },
  });
  return {
    queryClient,
  };
}

export function Provider({
  children,
  queryClient,
}: {
  children: React.ReactNode;
  queryClient: QueryClient;
}) {
  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}
