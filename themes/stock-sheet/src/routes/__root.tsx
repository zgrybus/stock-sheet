import { Outlet, createRootRouteWithContext } from "@tanstack/react-router";
import type { QueryClient } from "@tanstack/react-query";
import { useIsDesktop } from "@/features/match-media-utils/use-is-desktop/use-is-desktop";
import { DesktopHeader } from "@/features/header/desktop-header/desktop-header";
import { MobileHeader } from "@/features/header/mobile-header/mobile-header";
import { Sidebar } from "@/features/header/sidebar/sidebar";

type MyRouterContext = {
  queryClient: QueryClient;
};

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: RootComponent,
});

function RootComponent() {
  const { isDesktop } = useIsDesktop();

  return (
    // <div className="flex min-h-dvh">
    //   {isDesktop ? (
    //     <aside className="flex min-h-dvh w-60">
    //       <Sidebar />
    //     </aside>
    //   ) : null}
    //   <div className="flex flex-1 flex-col">
    //     {isDesktop ? <DesktopHeader /> : <MobileHeader />}
    //     <div
    //       className={`
    //         flex-1 bg-muted px-4 py-3
    //         lg:px-10 lg:py-6
    //       `}
    //     >
    //       <Outlet />
    //     </div>
    //   </div>
    // </div>
    <div
      className={`
        flex min-h-dvh w-full bg-background text-foreground antialiased
        selection:bg-primary/20 selection:text-primary
      `}
    >
      {isDesktop ? (
        <aside
          className={`
            sticky top-0 flex h-dvh w-64 flex-col border-r border-border
            bg-sidebar
          `}
        >
          <Sidebar />
        </aside>
      ) : null}

      <div className="flex min-w-0 flex-1 flex-col">
        <div
          className={`
            sticky top-0 z-50 border-b border-border bg-background/80
            backdrop-blur-md
          `}
        >
          {isDesktop ? <DesktopHeader /> : <MobileHeader />}
        </div>

        <main
          className={`
            flex-1 p-4
            lg:p-8
          `}
        >
          <Outlet />
        </main>
      </div>
    </div>
  );
}
