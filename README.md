# @MeikoTech/capacitor-square-inapp-payments

Integrate with Square Payments SDK

| Package Version | Capacitor Version |
| --------------- | ----------------- |
| 5.X             | 5.X               |

## Install

Version 5.X is compatible with Capacitor 5.X

```bash
npm install capacitor-square-inapp-payments
npx cap sync
```

## Usage

Card details response flow - React.js example

```js
import { CapacitorSquareInappPayments } from '@MeikoTech/capacitor-square-inapp-payments';

const Home = () => {
  const startCardEntry = async () => {
    await CapacitorSquareInappPayments.startCardPayment();
  };

  const startGooglePay = async () => {
    await CapacitorSquareInappPayments.startGooglePay({
      chargeAmount: '5.12',
      currencyCode: 'CAD',
    });
  };

  const startApplePay = async () => {
    await CapacitorSquareInappPayments.startApplePay({
      chargeAmount: '5.12',
      countryCode: 'CA',
      currencyCode: 'CAD',
      merchantId: 'merchant.com.spyce.delivery',
    });
  };
};
```

Follow these setup steps from square to enable call back to your app: [Square Documentation](https://developer.squareup.com/docs/pos-api/build-on-ios#step-4-add-your-url-schemes).

## API

<docgen-index>

* [`echo(...)`](#echo)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### startCardEntry(...)

```javascript
startCardEntry() => any
```

**Returns:** <code>any</code>

---

### startGooglePay(...)

```javascript
startGooglePay({ chargeAmount, currencyCode }) => any
```

| Param         | Type                                                         |
| ------------- | ------------------------------------------------------------ |
| **`options`** | <code>{ chargeAmount: string; currencyCode: string; }</code> |

**Returns:** <code>any</code>

---

### startApplePay(...)

```typescript
startApplePay({ chargeAmount, currencyCode, countryCode, merchantId }) => any
```

| Param         | Type                                                                                                  |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ chargeAmount: string; currencyCode: string; countryCode: string; merchantId: string; }</code> |

**Returns:** <code>any</code>

---

### addListener(...)

```javascript
addListener(eventName: 'cardDetailsSuccess', (cardDetails) => Promise
```

| Param              | Type                                                                            |
| ------------------ | ------------------------------------------------------------------------------- |
| **`eventName`**    | <code>"cardDetailsSuccess"</code>                                               |
| **`listenerFunc`** | <code>({ paymentMethod, cardNonce, cardBrand, cardLastFour }) =&gt; void</code> |

**Returns:** <code>any</code>

---

### Interfaces

#### PluginListenerHandle

| Prop         | Type                      |
| ------------ | ------------------------- |
| **`remove`** | <code>() =&gt; any</code> |
