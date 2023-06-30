import { WebPlugin } from '@capacitor/core';

import type { CapacitorSquareInappPaymentsPlugin } from './definitions';

export class CapacitorSquareInappPaymentsWeb extends WebPlugin implements CapacitorSquareInappPaymentsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
