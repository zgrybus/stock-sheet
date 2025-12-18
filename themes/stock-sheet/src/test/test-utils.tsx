/* eslint-disable react-refresh/only-export-components */
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render } from "@testing-library/react";
import {
  RouterProvider,
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
  defaultStringifySearch,
  interpolatePath,
} from "@tanstack/react-router";
import { act } from "react";
import type { ReactElement, ReactNode } from "react";
import type {
  RegisteredRouter,
  ValidateNavigateOptions,
} from "@tanstack/react-router";
import { routeTree } from "@/routeTree.gen";

const interpolateRoute = <
  TOptions,
  TRouter extends RegisteredRouter = RegisteredRouter
>(
  navigate: ValidateNavigateOptions<TRouter, TOptions>
) => {
  const path = interpolatePath({
    path: navigate.to as string,
    params: navigate.params as Record<string, unknown>,
  });

  return (
    path.interpolatedPath +
    defaultStringifySearch(navigate.search as Record<string, unknown>)
  );
};

const createTestQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

type TestProvidersProps = {
  children: ReactNode;
  queryClient?: QueryClient;
};

function TestProviders({
  queryClient = createTestQueryClient(),
  children,
}: TestProvidersProps) {
  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}

type RenderWithProvidersOptions = {
  queryClient: QueryClient;
};

export async function renderComponentWithRouterAndProviders<
  TOptions,
  TRouter extends RegisteredRouter = RegisteredRouter
>(
  ui: ReactElement,
  navigate?: ValidateNavigateOptions<TRouter, TOptions>,
  options?: RenderWithProvidersOptions
) {
  const rootRoute = createRootRoute({});
  const queryClient = options?.queryClient ?? createTestQueryClient();

  rootRoute.addChildren([
    createRoute({
      path: navigate?.to ?? ("/" as string),
      getParentRoute: () => rootRoute,
      component: () => ui,
      notFoundComponent: () => <div>Not Found</div>,
    }),
  ]);

  const router = createRouter({
    routeTree: rootRoute,
    history: createMemoryHistory({
      initialEntries: ["/"],
    }),
    context: {
      queryClient,
    },
  });

  router.navigate({ to: "/" });

  const res = render(<RouterProvider router={router} />, {
    wrapper: (props) => (
      <TestProviders queryClient={queryClient}>{props.children}</TestProviders>
    ),
  });

  const rerender = async () => {
    res.rerender(<RouterProvider router={router} />);
    await act(async () => {});
  };

  return await act(
    async () =>
      await {
        ...res,
        router,
        rerender,
      }
  );
}
type RenderAppOptions = {
  queryClient?: QueryClient;
};

export async function renderApp<
  TOptions,
  TRouter extends RegisteredRouter = RegisteredRouter
>(
  navigate: ValidateNavigateOptions<TRouter, TOptions>,
  options?: RenderAppOptions
) {
  const queryClient = options?.queryClient ?? createTestQueryClient();

  const router = createRouter({
    routeTree,
    context: {
      queryClient,
    },
    history: createMemoryHistory({
      initialEntries: [interpolateRoute(navigate)],
    }),
  });

  const result = render(
    <TestProviders queryClient={queryClient}>
      <RouterProvider router={router} />
    </TestProviders>
  );

  await act(() => router.navigate(navigate));

  const rerender = async () => {
    result.rerender(<RouterProvider router={router} />);
    await act(async () => {});
  };

  return await act(
    async () =>
      await {
        ...result,
        router,
        rerender,
      }
  );
}
