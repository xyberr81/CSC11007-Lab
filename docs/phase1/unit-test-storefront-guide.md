# Hướng dẫn: Viết và chạy Unit Test cho Storefront (Next.js)

---

## Cấu trúc source code cần test

```
storefront/utils/
├── concatQueryString.ts    ← pure function
├── formatPrice.ts          ← pure function
├── orderUtil.ts            ← 3 switch-case functions (dùng enum)
└── useDebounce.ts          ← React hook
```

---

## Bước 1: Cài dependencies

```powershell
cd storefront
npm install --save-dev jest jest-environment-jsdom @testing-library/react @testing-library/jest-dom @types/jest
```

---

## Bước 2: Tạo file cấu hình Jest

### `jest.config.js`

```javascript
const nextJest = require('next/jest')

const createJestConfig = nextJest({
  dir: './',
})

const customJestConfig = {
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  testEnvironment: 'jest-environment-jsdom',
  moduleDirectories: ['node_modules', '<rootDir>/'],
}

module.exports = createJestConfig(customJestConfig)
```

> ℹ️ `next/jest` tự động đọc `tsconfig.json` nên path alias `@/*` hoạt động mà không cần cấu hình thêm.

### `jest.setup.js`

```javascript
import '@testing-library/jest-dom'
```

---

## Bước 3: Thêm script test vào `package.json`

```json
"scripts": {
  "test": "jest",
  "test:watch": "jest --watch",
  "test:coverage": "jest --coverage"
}
```

---

## Bước 4: Viết Unit Test

### `utils/concatQueryString.test.ts`

```typescript
import { concatQueryString } from './concatQueryString';

describe('concatQueryString', () => {
  it('should return original url when array is empty', () => {
    const result = concatQueryString([], '/api/products');
    expect(result).toBe('/api/products');
  });

  it('should append single param with ? prefix', () => {
    const result = concatQueryString(['page=1'], '/api/products');
    expect(result).toBe('/api/products?page=1');
  });

  it('should append multiple params with ? and & prefixes', () => {
    const result = concatQueryString(['page=1', 'size=10'], '/api/products');
    expect(result).toBe('/api/products?page=1&size=10');
  });

  it('should handle three or more params', () => {
    const result = concatQueryString(['page=1', 'size=10', 'sort=name'], '/api/products');
    expect(result).toBe('/api/products?page=1&size=10&sort=name');
  });

  it('should work with empty string url', () => {
    const result = concatQueryString(['key=value'], '');
    expect(result).toBe('?key=value');
  });

  it('should preserve url that already has path', () => {
    const result = concatQueryString(['category=electronics'], 'http://localhost:3000/api/search');
    expect(result).toBe('http://localhost:3000/api/search?category=electronics');
  });
});
```

### `utils/formatPrice.test.ts`

```typescript
import { formatPrice } from './formatPrice';

describe('formatPrice', () => {
  it('should format integer price in USD', () => {
    expect(formatPrice(100)).toBe('$100.00');
  });

  it('should format decimal price in USD', () => {
    expect(formatPrice(49.99)).toBe('$49.99');
  });

  it('should format zero price', () => {
    expect(formatPrice(0)).toBe('$0.00');
  });

  it('should format large price with comma separator', () => {
    expect(formatPrice(1234567.89)).toBe('$1,234,567.89');
  });

  it('should format negative price', () => {
    expect(formatPrice(-25.5)).toBe('-$25.50');
  });

  it('should format small decimal price', () => {
    expect(formatPrice(0.99)).toBe('$0.99');
  });

  it('should round to 2 decimal places', () => {
    expect(formatPrice(10.999)).toBe('$11.00');
  });
});
```

### `utils/orderUtil.test.ts`

```typescript
import { getOrderStatusTitle, getDeliveryMethodTitle, getDeliveryStatusTitle } from './orderUtil';
import { EOrderStatus } from '@/modules/order/models/EOrderStatus';
import { EDeliveryMethod } from '@/modules/order/models/EDeliveryMethod';
import { EDeliveryStatus } from '@/modules/order/models/EDeliveryStatus';

describe('getOrderStatusTitle', () => {
  it('should return "Pending" for PENDING', () => {
    expect(getOrderStatusTitle(EOrderStatus.PENDING)).toBe('Pending');
  });
  it('should return "Accepted" for ACCEPTED', () => {
    expect(getOrderStatusTitle(EOrderStatus.ACCEPTED)).toBe('Accepted');
  });
  it('should return "Completed" for COMPLETED', () => {
    expect(getOrderStatusTitle(EOrderStatus.COMPLETED)).toBe('Completed');
  });
  it('should return "Cancelled" for CANCELLED', () => {
    expect(getOrderStatusTitle(EOrderStatus.CANCELLED)).toBe('Cancelled');
  });
  it('should return "Pending Payment" for PENDING_PAYMENT', () => {
    expect(getOrderStatusTitle(EOrderStatus.PENDING_PAYMENT)).toBe('Pending Payment');
  });
  it('should return "Paid" for PAID', () => {
    expect(getOrderStatusTitle(EOrderStatus.PAID)).toBe('Paid');
  });
  it('should return "Refund" for REFUND', () => {
    expect(getOrderStatusTitle(EOrderStatus.REFUND)).toBe('Refund');
  });
  it('should return "Shipping" for SHIPPING', () => {
    expect(getOrderStatusTitle(EOrderStatus.SHIPPING)).toBe('Shipping');
  });
  it('should return "Reject" for REJECT', () => {
    expect(getOrderStatusTitle(EOrderStatus.REJECT)).toBe('Reject');
  });
  it('should return "All" for null', () => {
    expect(getOrderStatusTitle(null)).toBe('All');
  });
});

describe('getDeliveryMethodTitle', () => {
  it('should return "Grab Express" for GRAB_EXPRESS', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.GRAB_EXPRESS)).toBe('Grab Express');
  });
  it('should return "Viettel Post" for VIETTEL_POST', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.VIETTEL_POST)).toBe('Viettel Post');
  });
  it('should return "Shopee Express" for SHOPEE_EXPRESS', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.SHOPEE_EXPRESS)).toBe('Shopee Express');
  });
  it('should return "Yas Express" for YAS_EXPRESS', () => {
    expect(getDeliveryMethodTitle(EDeliveryMethod.YAS_EXPRESS)).toBe('Yas Express');
  });
  it('should return "Preparing" for unknown value', () => {
    expect(getDeliveryMethodTitle('UNKNOWN' as EDeliveryMethod)).toBe('Preparing');
  });
});

describe('getDeliveryStatusTitle', () => {
  it('should return "Cancelled" for CANCELLED', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.CANCELLED)).toBe('Cancelled');
  });
  it('should return "Delivered" for DELIVERED', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.DELIVERED)).toBe('Delivered');
  });
  it('should return "Delivering" for DELIVERING', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.DELIVERING)).toBe('Delivering');
  });
  it('should return "Pending" for PENDING', () => {
    expect(getDeliveryStatusTitle(EDeliveryStatus.PENDING)).toBe('Pending');
  });
  it('should return "Preparing" for unknown value', () => {
    expect(getDeliveryStatusTitle('UNKNOWN' as EDeliveryStatus)).toBe('Preparing');
  });
});
```

### `utils/useDebounce.test.ts`

```typescript
import { renderHook, act } from '@testing-library/react';
import { useDebounce } from './useDebounce';

describe('useDebounce', () => {
  beforeEach(() => { jest.useFakeTimers(); });
  afterEach(() => { jest.useRealTimers(); });

  it('should return initial value immediately', () => {
    const { result } = renderHook(() => useDebounce('hello', 500));
    expect(result.current).toBe('hello');
  });

  it('should not update value before delay', () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: 'hello', delay: 500 } }
    );
    rerender({ value: 'world', delay: 500 });
    act(() => { jest.advanceTimersByTime(300); });
    expect(result.current).toBe('hello');
  });

  it('should update value after delay', () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: 'hello', delay: 500 } }
    );
    rerender({ value: 'world', delay: 500 });
    act(() => { jest.advanceTimersByTime(500); });
    expect(result.current).toBe('world');
  });

  it('should reset timer on rapid value changes', () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: 'a', delay: 500 } }
    );
    rerender({ value: 'ab', delay: 500 });
    act(() => { jest.advanceTimersByTime(300); });
    rerender({ value: 'abc', delay: 500 });
    act(() => { jest.advanceTimersByTime(300); });
    expect(result.current).toBe('a');
    act(() => { jest.advanceTimersByTime(200); });
    expect(result.current).toBe('abc');
  });

  it('should handle undefined value', () => {
    const { result } = renderHook(() => useDebounce(undefined, 500));
    expect(result.current).toBeUndefined();
  });

  it('should handle empty string', () => {
    const { result } = renderHook(() => useDebounce('', 300));
    expect(result.current).toBe('');
  });
});
```

---

## Bước 5: Chạy test và xem coverage

```powershell
npm test                # chạy test
npm run test:coverage   # chạy test + coverage report
```

Xem báo cáo coverage HTML:

```powershell
Invoke-Item coverage\lcov-report\index.html
```

**Kết quả đạt được:**

```
-----------------------|---------|----------|---------|---------|---
File                   | % Stmts | % Branch | % Funcs | % Lines |
-----------------------|---------|----------|---------|---------|---
All files              |     100 |      100 |     100 |     100 |
 modules/order/models  |     100 |      100 |     100 |     100 |
 utils                 |     100 |      100 |     100 |     100 |
-----------------------|---------|----------|---------|---------|---
Test Suites: 4 passed, 4 total
Tests:       39 passed, 39 total
```

✅ **100% coverage trên tất cả metrics**

---

## Tổng kết

| File test                      | File source            | Số tests |
| ------------------------------ | ---------------------- | -------- |
| `concatQueryString.test.ts`    | `concatQueryString.ts` | 6        |
| `formatPrice.test.ts`          | `formatPrice.ts`       | 7        |
| `orderUtil.test.ts`            | `orderUtil.ts`         | 20       |
| `useDebounce.test.ts`          | `useDebounce.ts`       | 6        |
| **Tổng**                       |                        | **39**   |
