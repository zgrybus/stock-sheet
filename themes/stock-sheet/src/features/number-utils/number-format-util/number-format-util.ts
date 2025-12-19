export const numberFormatUtil = (options: Intl.NumberFormatOptions) =>
  new Intl.NumberFormat(navigator.language, options);

export const priceFormatUtil = numberFormatUtil({
  style: "currency",
  currency: "PLN",
});
