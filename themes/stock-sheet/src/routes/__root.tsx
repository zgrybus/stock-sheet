import { Outlet, createRootRouteWithContext } from "@tanstack/react-router";
import type { QueryClient } from "@tanstack/react-query";
import { useIsDesktop } from "@/features/match-media-utils/use-is-desktop/use-is-desktop";
import { DesktopSidebar } from "@/features/sidebar/desktop-sidebar/desktop-sidebar";
import { MobileSidebar } from "@/features/sidebar/mobile-sidebar/mobile-sidebar";
import { DesktopHeader } from "@/features/header/desktop-header/desktop-header";
import { MobileHeader } from "@/features/header/mobile-header/mobile-header";

type MyRouterContext = {
  queryClient: QueryClient;
};

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: RootComponent,
});

function RootComponent() {
  const { isDesktop } = useIsDesktop();

  return (
    <div className="flex">
      {isDesktop ? <DesktopSidebar /> : <MobileSidebar />}
      <div className="flex-1">
        {isDesktop ? <DesktopHeader /> : <MobileHeader />}
        <div
          className={`
            px-4 py-3
            lg:px-10 lg:py-6
          `}
        >
          <Outlet />
        </div>
      </div>
    </div>
  );
}
