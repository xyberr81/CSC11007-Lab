# Hướng dẫn: Viết và chạy Unit Test cho Backoffice (Next.js)

---

## Cấu trúc source code cần test

```
backoffice/
├── utils/
│   ├── concatQueryString.ts         ← pure function
│   └── formatPrice.ts               ← 2 functions: formatPriceVND, formatPriceUSD
├── common/services/
│   ├── ToastService.ts              ← toast wrapper (mock react-toastify)
│   └── ResponseStatusHandlingService.ts  ← 4 handler functions
└── constants/
    └── Common.ts                    ← constants, URLs, mappings
```

---

## Bước 1: Cài dependencies

```powershell
cd backoffice
npm install --save-dev jest jest-environment-jsdom @testing-library/react @testing-library/jest-dom @testing-library/user-event @types/jest
```

---

## Bước 2: Tạo file cấu hình Jest

### `jest.config.js`

> ⚠️ Backoffice dùng nhiều path alias phức tạp trong `tsconfig.json`. Phải map tất cả trong `moduleNameMapper`.

```javascript
const nextJest = require('next/jest')

const createJestConfig = nextJest({
  dir: './',
})

const customJestConfig = {
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  testEnvironment: 'jest-environment-jsdom',
  moduleDirectories: ['node_modules', '<rootDir>/'],
  moduleNameMapper: {
    '^@commonServices/(.*)$': '<rootDir>/common/services/$1',
    '^@commonItems/(.*)$': '<rootDir>/common/items/$1',
    '^@constants/(.*)$': '<rootDir>/constants/$1',
    '^@catalogModels/(.*)$': '<rootDir>/modules/catalog/models/$1',
    '^@catalogServices/(.*)$': '<rootDir>/modules/catalog/services/$1',
    '^@catalogComponents/(.*)$': '<rootDir>/modules/catalog/components/$1',
    '^@locationComponents/(.*)$': '<rootDir>/modules/location/components/$1',
    '^@locationModels/(.*)$': '<rootDir>/modules/location/models/$1',
    '^@locationServices/(.*)$': '<rootDir>/modules/location/services/$1',
    '^@taxServices/(.*)$': '<rootDir>/modules/tax/services/$1',
    '^@taxComponents/(.*)$': '<rootDir>/modules/tax/components/$1',
    '^@taxModels/(.*)$': '<rootDir>/modules/tax/models/$1',
    '^@inventoryServices/(.*)$': '<rootDir>/modules/inventory/services/$1',
    '^@inventoryModels/(.*)$': '<rootDir>/modules/inventory/models/$1',
    '^@inventoryComponents/(.*)$': '<rootDir>/modules/inventory/components/$1',
    '^@webhookComponents/(.*)$': '<rootDir>/modules/webhook/components/$1',
    '^@webhookServices/(.*)$': '<rootDir>/modules/webhook/services/$1',
    '^@webhookModels/(.*)$': '<rootDir>/modules/webhook/models/$1',
  },
}

module.exports = createJestConfig(customJestConfig)
```

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
  "test:coverage": "jest --coverage",
  "lint": "next lint"
}
```

---

## Bước 4: Viết Unit Test

### `utils/concatQueryString.test.ts`

```typescript
import { concatQueryString } from './concatQueryString';

describe('concatQueryString', () => {
  it('should return original url when array is empty', () => {
    expect(concatQueryString([], '/api/products')).toBe('/api/products');
  });
  it('should append single param with ? prefix', () => {
    expect(concatQueryString(['page=1'], '/api/products')).toBe('/api/products?page=1');
  });
  it('should append multiple params with ? and & prefixes', () => {
    expect(concatQueryString(['page=1', 'size=10'], '/api/products')).toBe('/api/products?page=1&size=10');
  });
  it('should handle three or more params', () => {
    expect(concatQueryString(['page=1', 'size=10', 'sort=name'], '/api/products')).toBe('/api/products?page=1&size=10&sort=name');
  });
  it('should work with empty string url', () => {
    expect(concatQueryString(['key=value'], '')).toBe('?key=value');
  });
  it('should preserve url that already has path', () => {
    expect(concatQueryString(['category=electronics'], 'http://localhost:3001/api/search')).toBe('http://localhost:3001/api/search?category=electronics');
  });
});
```

### `utils/formatPrice.test.ts`

```typescript
import { formatPriceVND, formatPriceUSD } from './formatPrice';

describe('formatPriceVND', () => {
  it('should format integer price in VND', () => {
    expect(formatPriceVND(100000)).toContain('100.000');
  });
  it('should format zero price', () => {
    expect(formatPriceVND(0)).toContain('0');
  });
  it('should format large price in VND', () => {
    expect(formatPriceVND(1234567)).toContain('1.234.567');
  });
  it('should format negative price in VND', () => {
    expect(formatPriceVND(-50000)).toContain('50.000');
  });
  it('should return a string containing VND currency symbol', () => {
    expect(formatPriceVND(1000)).toContain('₫');
  });
});

describe('formatPriceUSD', () => {
  it('should format integer price in USD', () => {
    expect(formatPriceUSD(100)).toBe('$100.00');
  });
  it('should format decimal price in USD', () => {
    expect(formatPriceUSD(49.99)).toBe('$49.99');
  });
  it('should format zero price', () => {
    expect(formatPriceUSD(0)).toBe('$0.00');
  });
  it('should format large price with comma separator', () => {
    expect(formatPriceUSD(1234567.89)).toBe('$1,234,567.89');
  });
  it('should format negative price', () => {
    expect(formatPriceUSD(-25.5)).toBe('-$25.50');
  });
  it('should round to 2 decimal places', () => {
    expect(formatPriceUSD(10.999)).toBe('$11.00');
  });
});
```

### `common/services/ToastService.test.ts`

```typescript
import { toast } from 'react-toastify';
import { toastSuccess, toastError } from './ToastService';

jest.mock('react-toastify', () => ({
  toast: { success: jest.fn(), error: jest.fn() },
}));

describe('ToastService', () => {
  beforeEach(() => { jest.clearAllMocks(); });

  describe('toastSuccess', () => {
    it('should call toast.success with message and default options', () => {
      toastSuccess('Operation successful');
      expect(toast.success).toHaveBeenCalledWith('Operation successful', {
        position: 'top-right', autoClose: 3000, closeOnClick: true, pauseOnHover: false, theme: 'colored',
      });
    });
    it('should call toast.success with custom options when provided', () => {
      const customOptions = { autoClose: 5000 };
      toastSuccess('Custom toast', customOptions);
      expect(toast.success).toHaveBeenCalledWith('Custom toast', customOptions);
    });
  });

  describe('toastError', () => {
    it('should call toast.error with message and default options', () => {
      toastError('Something went wrong');
      expect(toast.error).toHaveBeenCalledWith('Something went wrong', {
        position: 'top-right', autoClose: 3000, closeOnClick: true, pauseOnHover: false, theme: 'colored',
      });
    });
    it('should call toast.error with custom options when provided', () => {
      const customOptions = { position: 'bottom-left' as const };
      toastError('Error occurred', customOptions);
      expect(toast.error).toHaveBeenCalledWith('Error occurred', customOptions);
    });
  });
});
```

### `common/services/ResponseStatusHandlingService.test.ts`

```typescript
import { handleDeletingResponse, handleUpdatingResponse, handleCreatingResponse, handleResponse } from './ResponseStatusHandlingService';
import { toastSuccess, toastError } from './ToastService';

jest.mock('./ToastService', () => ({ toastSuccess: jest.fn(), toastError: jest.fn() }));

describe('ResponseStatusHandlingService', () => {
  beforeEach(() => { jest.clearAllMocks(); });

  describe('handleDeletingResponse', () => {
    it('should show success toast when status is 204', () => {
      handleDeletingResponse({ status: 204 }, 'Product');
      expect(toastSuccess).toHaveBeenCalledWith('Product have been deleted');
    });
    it('should show error detail when title is Not Found', () => {
      handleDeletingResponse({ title: 'Not Found', detail: 'Item not found' }, 'Product');
      expect(toastError).toHaveBeenCalledWith('Item not found');
    });
    it('should show error detail when title is Bad Request', () => {
      handleDeletingResponse({ title: 'Bad Request', detail: 'Invalid ID' }, 'Product');
      expect(toastError).toHaveBeenCalledWith('Invalid ID');
    });
    it('should show generic delete failed for other errors', () => {
      handleDeletingResponse({ status: 500 }, 'Product');
      expect(toastError).toHaveBeenCalledWith('Delete failed');
    });
  });

  describe('handleUpdatingResponse', () => {
    it('should show success toast when status is 204', () => {
      handleUpdatingResponse({ status: 204 });
      expect(toastSuccess).toHaveBeenCalledWith('Update successfully');
    });
    it('should show error detail when title is Bad Request', () => {
      handleUpdatingResponse({ title: 'Bad Request', detail: 'Validation error' });
      expect(toastError).toHaveBeenCalledWith('Validation error');
    });
    it('should show error detail when title is Not Found', () => {
      handleUpdatingResponse({ title: 'Not Found', detail: 'Resource not found' });
      expect(toastError).toHaveBeenCalledWith('Resource not found');
    });
    it('should show generic update failed for other errors', () => {
      handleUpdatingResponse({ status: 500 });
      expect(toastError).toHaveBeenCalledWith('Update failed');
    });
  });

  describe('handleCreatingResponse', () => {
    it('should show success toast when status is 201', async () => {
      await handleCreatingResponse({ status: 201 });
      expect(toastSuccess).toHaveBeenCalledWith('Create successfully');
    });
    it('should show error detail when status is 400', async () => {
      const mockResponse = { status: 400, json: jest.fn().mockResolvedValue({ detail: 'Name is required' }) };
      await handleCreatingResponse(mockResponse);
      expect(toastError).toHaveBeenCalledWith('Name is required');
    });
    it('should show generic create failed for other errors', async () => {
      await handleCreatingResponse({ status: 500 });
      expect(toastError).toHaveBeenCalledWith('Create failed');
    });
  });

  describe('handleResponse', () => {
    it('should show success message when response is ok', () => {
      handleResponse({ ok: true }, 'Saved!', 'Failed to save');
      expect(toastSuccess).toHaveBeenCalledWith('Saved!');
    });
    it('should show error message when response is not ok', () => {
      handleResponse({ ok: false }, 'Saved!', 'Failed to save');
      expect(toastError).toHaveBeenCalledWith('Failed to save');
    });
  });
});
```

### `constants/Common.test.ts`

> ℹ️ File `Common.ts` chỉ chứa constants (không có logic), nhưng cần test để đạt 100% statement coverage vì JaCoCo/Jest tính cả các dòng khai báo.

```typescript
import {
  ToastVariant, ResponseStatus, ResponseTitle,
  HAVE_BEEN_DELETED, DELETE_FAILED, UPDATE_SUCCESSFULLY, CREATE_SUCCESSFULLY,
  UPDATE_FAILED, CREATE_FAILED, ADD_PRODUCT_INTO_WAREHOUSE_SUCCESSFULLY,
  TOAST_DURATION, CATEGORIES_URL, BRAND_URL, CUSTOMER_URL,
  PRODUCT_ATTRIBUTE_GROUPS_URL, PRODUCT_OPTIONS_URL, PRODUCT_ATTRIBUTE_URL,
  PRODUCT_TEMPLATE_URL, PRODUCT_URL, COUNTRY_URL, STATE_OR_PROVINCE_URL,
  TAX_CLASS_URL, TAX_RATE_URL, INVENTORY_WAREHOUSE_PRODUCTS_URL,
  WAREHOUSE_URL, INVENTORY_WAREHOUSE_STOCKS_URL,
  INVENTORY_WAREHOUSE_STOCKS_HISTORIES_URL, SALES_ORDERS_URL,
  SALES_SHIPMENTS_URL, SALES_RETURN_REQUESTS_URL,
  SALES_RECURRING_PAYMENTS_URL, SALES_GIFT_CARDS_URL,
  SALES_SHOPPING_CARTS_AND_WISHLISTS_URL, MANAGER_PROMOTIONS_URL,
  SYSTEM_PAYMENT_PROVIDERS, SYSTEM_SETTINGS, WEBHOOKS_URL,
  DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER, FORMAT_DATE_YYYY_MM_DD_HH_MM,
  mappingExportingProductColumnNames,
} from './Common';

describe('Common constants', () => {
  it('ToastVariant should have correct values', () => {
    expect(ToastVariant.SUCCESS).toBe('success');
    expect(ToastVariant.WARNING).toBe('warning');
    expect(ToastVariant.ERROR).toBe('error');
  });

  it('ResponseStatus should have correct HTTP status codes', () => {
    expect(ResponseStatus.CREATED).toBe(201);
    expect(ResponseStatus.SUCCESS).toBe(204);
    expect(ResponseStatus.NOT_FOUND).toBe(404);
    expect(ResponseStatus.BAD_REQUEST).toBe(400);
  });

  it('ResponseTitle should have correct titles', () => {
    expect(ResponseTitle.NOT_FOUND).toBe('Not Found');
    expect(ResponseTitle.BAD_REQUEST).toBe('Bad Request');
  });

  it('Message constants should be correct', () => {
    expect(HAVE_BEEN_DELETED).toBe(' have been deleted');
    expect(DELETE_FAILED).toBe('Delete failed');
    expect(UPDATE_SUCCESSFULLY).toBe('Update successfully');
    expect(CREATE_SUCCESSFULLY).toBe('Create successfully');
    expect(UPDATE_FAILED).toBe('Update failed');
    expect(CREATE_FAILED).toBe('Create failed');
    expect(ADD_PRODUCT_INTO_WAREHOUSE_SUCCESSFULLY).toBe('Add product(s) into warehouse successfully');
  });

  it('Configuration constants should be correct', () => {
    expect(TOAST_DURATION).toBe(4000);
    expect(DEFAULT_PAGE_SIZE).toBe(10);
    expect(DEFAULT_PAGE_NUMBER).toBe(0);
    expect(FORMAT_DATE_YYYY_MM_DD_HH_MM).toBe('YYYYMMDDHHmmss');
  });

  it('URL constants should have correct paths', () => {
    expect(CATEGORIES_URL).toBe('/catalog/categories');
    expect(BRAND_URL).toBe('/catalog/brands');
    expect(CUSTOMER_URL).toBe('/customers');
    expect(PRODUCT_URL).toBe('/catalog/products');
    expect(COUNTRY_URL).toBe('/location/countries');
    expect(TAX_CLASS_URL).toBe('/tax/tax-classes');
    expect(WAREHOUSE_URL).toBe('/inventory/warehouses');
    expect(SALES_ORDERS_URL).toBe('/sales/orders');
    expect(WEBHOOKS_URL).toBe('/webhook');
    // Verify remaining URLs are defined
    expect(PRODUCT_ATTRIBUTE_GROUPS_URL).toBeDefined();
    expect(PRODUCT_OPTIONS_URL).toBeDefined();
    expect(PRODUCT_ATTRIBUTE_URL).toBeDefined();
    expect(PRODUCT_TEMPLATE_URL).toBeDefined();
    expect(STATE_OR_PROVINCE_URL).toBeDefined();
    expect(TAX_RATE_URL).toBeDefined();
    expect(INVENTORY_WAREHOUSE_PRODUCTS_URL).toBeDefined();
    expect(INVENTORY_WAREHOUSE_STOCKS_URL).toBeDefined();
    expect(INVENTORY_WAREHOUSE_STOCKS_HISTORIES_URL).toBeDefined();
    expect(SALES_SHIPMENTS_URL).toBeDefined();
    expect(SALES_RETURN_REQUESTS_URL).toBeDefined();
    expect(SALES_RECURRING_PAYMENTS_URL).toBeDefined();
    expect(SALES_GIFT_CARDS_URL).toBeDefined();
    expect(SALES_SHOPPING_CARTS_AND_WISHLISTS_URL).toBeDefined();
    expect(MANAGER_PROMOTIONS_URL).toBeDefined();
    expect(SYSTEM_PAYMENT_PROVIDERS).toBeDefined();
    expect(SYSTEM_SETTINGS).toBeDefined();
  });

  it('mappingExportingProductColumnNames should have correct mappings', () => {
    expect(mappingExportingProductColumnNames.id).toBe('Id');
    expect(mappingExportingProductColumnNames.name).toBe('Product Name');
    expect(mappingExportingProductColumnNames.price).toBe('Price');
    expect(mappingExportingProductColumnNames.sku).toBe('SKU');
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
-----------------------------------|---------|----------|---------|---------|---
File                               | % Stmts | % Branch | % Funcs | % Lines |
-----------------------------------|---------|----------|---------|---------|---
All files                          |     100 |      100 |     100 |     100 |
 common/services                   |     100 |      100 |     100 |     100 |
  ResponseStatusHandlingService.ts |     100 |      100 |     100 |     100 |
  ToastService.ts                  |     100 |      100 |     100 |     100 |
 constants                         |     100 |      100 |     100 |     100 |
  Common.ts                        |     100 |      100 |     100 |     100 |
 utils                             |     100 |      100 |     100 |     100 |
  concatQueryString.ts             |     100 |      100 |     100 |     100 |
  formatPrice.ts                   |     100 |      100 |     100 |     100 |
-----------------------------------|---------|----------|---------|---------|---
Test Suites: 5 passed, 5 total
Tests:       43 passed, 43 total
```

✅ **100% coverage trên tất cả metrics**

---

## Lỗi đã gặp và cách fix

| Lỗi | Nguyên nhân | Cách fix |
| --- | --- | --- |
| `Unknown option "setupFilesAfterSetup"` | Viết sai tên config key | Đổi thành `setupFilesAfterEnv` |
| `Stmts 75.55%` do `Common.ts` | Constants chưa được import hết trong test | Thêm `constants/Common.test.ts` |
| `Haste module naming collision: backoffice` | Thư mục `.next/standalone` có `package.json` trùng tên | Warning vô hại, không ảnh hưởng test |

---

## Tổng kết

| File test                                  | File source                          | Số tests |
| ------------------------------------------ | ------------------------------------ | -------- |
| `utils/concatQueryString.test.ts`          | `utils/concatQueryString.ts`         | 6        |
| `utils/formatPrice.test.ts`               | `utils/formatPrice.ts`               | 11       |
| `common/services/ToastService.test.ts`     | `common/services/ToastService.ts`    | 4        |
| `common/services/ResponseStatus...test.ts` | `common/services/ResponseStatus...`  | 13       |
| `constants/Common.test.ts`                 | `constants/Common.ts`                | 9        |
| **Tổng**                                   |                                      | **43**   |
