import type { XtbXlsxRowType } from "./use-xtb-xlsx-parser";

export const MockXtbXlsxParserData: Array<XtbXlsxRowType> = [
  {
    __rowNum__: 5,
    __EMPTY: "",
    __EMPTY_2: "",
    __EMPTY_3: "",
    __EMPTY_4: "USD",
    __EMPTY_5: "",
    __EMPTY_1: "",
  },
  // start of the table
  {
    __rowNum__: 11,
    __EMPTY: "",
    __EMPTY_2: "",
    __EMPTY_3: "",
    __EMPTY_4: "",
    __EMPTY_5: "",
    __EMPTY_1: "",
  },
  // Stock Purchase
  {
    __rowNum__: 12,
    __EMPTY: "1000001",
    __EMPTY_1: "Stock purchase",
    __EMPTY_2: "2023-01-15 14:30:00",
    __EMPTY_3: "OPEN BUY 10 @ 150.00",
    __EMPTY_4: "AAPL.US",
    __EMPTY_5: "-1500.00",
  },
  {
    __rowNum__: 13,
    __EMPTY: "1000002",
    __EMPTY_1: "Stock purchase",
    __EMPTY_2: "2023-02-10 10:15:00",
    __EMPTY_3: "OPEN BUY 5.5 @ 300.20",
    __EMPTY_4: "MSFT.US",
    __EMPTY_5: "-1651.10",
  },

  // Stock Sale
  {
    __rowNum__: 14,
    __EMPTY: "2000001",
    __EMPTY_1: "Stock sale",
    __EMPTY_2: "2023-06-20 15:00:00",
    __EMPTY_3: "CLOSE BUY 10 @ 175.50",
    __EMPTY_4: "AAPL.US",
    __EMPTY_5: "1755.00",
  },
  {
    __rowNum__: 15,
    __EMPTY: "2000002",
    __EMPTY_1: "Stock sale",
    __EMPTY_2: "2023-07-01 11:00:00",
    __EMPTY_3: "CLOSE BUY 5.5 @ 320.00",
    __EMPTY_4: "MSFT.US",
    __EMPTY_5: "1760.00",
  },

  // Close Trade
  {
    __rowNum__: 16,
    __EMPTY: "3000001",
    __EMPTY_1: "close trade",
    __EMPTY_2: "2023-06-20 15:00:00",
    __EMPTY_3: "Profit of position #1000001",
    __EMPTY_4: "AAPL.US",
    __EMPTY_5: "255.00",
  },
  {
    __rowNum__: 17,
    __EMPTY: "3000002",
    __EMPTY_1: "close trade",
    __EMPTY_2: "2023-07-01 11:00:00",
    __EMPTY_3: "Profit of position #1000002",
    __EMPTY_4: "MSFT.US",
    __EMPTY_5: "108.90",
  },
];
