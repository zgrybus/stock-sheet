export const dateTimeFormatUtil = (options: Intl.DateTimeFormatOptions) =>
  new Intl.DateTimeFormat(navigator.language, options);
