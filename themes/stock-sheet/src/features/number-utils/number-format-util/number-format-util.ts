export const numberFormatUtil = (options: Intl.NumberFormatOptions) =>
  new Intl.NumberFormat(navigator.language, options);

export const formatStockPrice = (
  value: number,
  currency: string | undefined,
) => {
  const fractionDigits = Math.abs(value) < 1 && value !== 0 ? 4 : 2;

  return numberFormatUtil({
    style: "currency",
    currency: currency,
    minimumFractionDigits: fractionDigits,
    maximumFractionDigits: fractionDigits,
  }).format(value);
};

export const isValidCurrency = (code: string): boolean => {
  try {
    new Intl.NumberFormat(undefined, { style: "currency", currency: code });
    return true;
  } catch (_e) {
    return false;
  }
};
