import { useMemo, useSyncExternalStore } from "react";

export const useIsDesktop = () => {
  const query = useMemo(() => window.matchMedia(`(min-width: 1024px)`), []);

  const subscribe = (callback: () => void) => {
    query.addEventListener("change", callback);
    return () => query.removeEventListener("change", callback);
  };

  const getSnapshot = () => query.matches;

  const isDesktop = useSyncExternalStore(subscribe, getSnapshot);

  return { isDesktop };
};
