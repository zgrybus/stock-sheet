import { renderHook, act, waitFor } from "@testing-library/react";
import { useMinimumLoadingTime } from "./use-minimum-loading-time";

describe("useMinimumLoadingTime", () => {
  beforeEach(() => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("initializes with isLoading as false", () => {
    const { result } = renderHook(() => useMinimumLoadingTime({ time: 500 }));
    expect(result.current.isLoading).toBe(false);
  });

  it("maintains loading state for the minimum time when the action resolves immediately", async () => {
    const MIN_TIME = 500;
    const { result } = renderHook(() =>
      useMinimumLoadingTime({ time: MIN_TIME })
    );

    const fastAction = vi.fn().mockResolvedValue("success");
    let promise!: Promise<unknown>;

    act(() => {
      promise = result.current.runWithDelay(fastAction);
    });

    expect(result.current.isLoading).toBe(true);

    act(() => {
      vi.advanceTimersByTime(200);
    });

    expect(result.current.isLoading).toBe(true);

    act(() => {
      vi.advanceTimersByTime(400);
    });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });
    await expect(promise).resolves.toBe("success");
  });

  it("extends loading state beyond minimum time if the action takes longer", async () => {
    const MIN_TIME = 100;
    const ACTION_TIME = 500;
    const { result } = renderHook(() =>
      useMinimumLoadingTime({ time: MIN_TIME })
    );

    const slowAction = () =>
      new Promise((resolve) =>
        setTimeout(() => resolve("slow result"), ACTION_TIME)
      );

    let promise!: Promise<unknown>;

    act(() => {
      promise = result.current.runWithDelay(slowAction);
    });

    expect(result.current.isLoading).toBe(true);

    act(() => {
      vi.advanceTimersByTime(MIN_TIME);
    });

    expect(result.current.isLoading).toBe(true);

    act(() => {
      vi.advanceTimersByTime(ACTION_TIME - MIN_TIME);
    });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });
    await expect(promise).resolves.toBe("slow result");
  });

  it("waits for the minimum time before rejecting when the action throws an error", async () => {
    const MIN_TIME = 500;
    const { result } = renderHook(() =>
      useMinimumLoadingTime({ time: MIN_TIME })
    );

    const errorAction = vi.fn().mockRejectedValue(new Error("Failure"));

    let promise!: Promise<unknown>;

    act(() => {
      promise = result.current.runWithDelay(errorAction);
    });

    promise.catch(() => {});

    expect(result.current.isLoading).toBe(true);

    act(() => {
      vi.advanceTimersByTime(200);
    });

    expect(result.current.isLoading).toBe(true);

    act(() => {
      vi.runAllTimers();
    });

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
    });
    await expect(promise).rejects.toThrow("Failure");
  });
});
