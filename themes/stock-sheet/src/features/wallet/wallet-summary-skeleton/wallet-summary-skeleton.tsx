import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

export const WalletSummarySkeleton = () => {
  return (
    <div
      className="mt-6 flex flex-col gap-4"
      data-testid="wallet-summary-skeleton"
    >
      <div
        className={`
          flex flex-col gap-4
          md:flex-row
        `}
      >
        <Card className="flex-1">
          <CardHeader className={`flex flex-row items-center justify-between`}>
            <Skeleton className="h-5 w-35" />
            <Skeleton className="size-4 rounded-full" />
          </CardHeader>
          <CardContent>
            <Skeleton className="mb-4 h-9 w-50" />
            <Skeleton className="mb-2 h-2 w-full rounded-full" />
            <div className="flex justify-between">
              <Skeleton className="h-3 w-20" />
              <Skeleton className="h-3 w-20" />
            </div>
          </CardContent>
        </Card>

        <Card className="flex-1">
          <CardHeader className={`flex flex-row items-center justify-between`}>
            <Skeleton className="h-5 w-62.5" />
            <Skeleton className="size-4 rounded-full" />
          </CardHeader>
          <CardContent>
            <Skeleton className="mb-4 h-9 w-50" />
            <Skeleton className="h-4 w-62.5" />
          </CardContent>
        </Card>
      </div>

      <div
        className={`
          flex flex-col gap-4
          md:flex-row
        `}
      >
        <Card className="flex-1">
          <CardHeader className={`flex flex-row items-center justify-between`}>
            <Skeleton className="h-5 w-30" />
            <Skeleton className="size-4 rounded-full" />
          </CardHeader>
          <CardContent>
            <Skeleton className="mb-4 h-9 w-45" />
            <Skeleton className="h-5 w-35" />
          </CardContent>
        </Card>

        <Card className="flex-1">
          <CardHeader className={`flex flex-row items-center justify-between`}>
            <Skeleton className="h-5 w-30" />
            <Skeleton className="size-4 rounded-full" />
          </CardHeader>
          <CardContent>
            <Skeleton className="mb-4 h-9 w-45" />
            <Skeleton className="h-5 w-35" />
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
