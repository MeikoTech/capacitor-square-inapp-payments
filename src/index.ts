import { registerPlugin } from '@capacitor/core';

import type { CapacitorSquareInappPaymentsPlugin } from './definitions';

const CapacitorSquareInappPayments = registerPlugin<CapacitorSquareInappPaymentsPlugin>('CapacitorSquareInappPayments', {
  web: () => import('./web').then(m => new m.CapacitorSquareInappPaymentsWeb()),
});

export * from './definitions';
export { CapacitorSquareInappPayments };
