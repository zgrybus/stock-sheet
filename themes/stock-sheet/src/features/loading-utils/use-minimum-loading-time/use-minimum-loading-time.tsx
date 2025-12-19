import { useState, useCallback } from "react";

type UseMinimumLoadingTimeProps = {
  time: number;
};

export const useMinimumLoadingTime = ({ time }: UseMinimumLoadingTimeProps) => {
  const [isLoading, setIsLoading] = useState(false);

  const runWithDelay = useCallback(
    async <T,>(action: () => Promise<T>): Promise<T> => {
      setIsLoading(true);

      const delayPromise = new Promise((resolve) => setTimeout(resolve, time));

      const actionPromise = action();

      try {
        const [result] = await Promise.all([actionPromise, delayPromise]);
        return result;
      } catch (error) {
        await delayPromise;
        throw error;
      } finally {
        setIsLoading(false);
      }
    },
    [time]
  );

  return { isLoading, runWithDelay };
};
