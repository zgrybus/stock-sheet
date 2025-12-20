export const numberFormatUtil = (options: Intl.NumberFormatOptions) =>
  new Intl.NumberFormat(navigator.language, options);

export const priceFormatUtil = numberFormatUtil({
  style: "currency",
  currency: "PLN",
});

export const isValidCurrency = (code: string): boolean => {
  try {
    new Intl.NumberFormat(undefined, { style: "currency", currency: code });
    return true;
  } catch (_e) {
    return false;
  }
};
