import {
  ToastVariant,
  ResponseStatus,
  ResponseTitle,
  HAVE_BEEN_DELETED,
  DELETE_FAILED,
  UPDATE_SUCCESSFULLY,
  CREATE_SUCCESSFULLY,
  UPDATE_FAILED,
  CREATE_FAILED,
  ADD_PRODUCT_INTO_WAREHOUSE_SUCCESSFULLY,
  TOAST_DURATION,
  CATEGORIES_URL,
  BRAND_URL,
  CUSTOMER_URL,
  PRODUCT_ATTRIBUTE_GROUPS_URL,
  PRODUCT_OPTIONS_URL,
  PRODUCT_ATTRIBUTE_URL,
  PRODUCT_TEMPLATE_URL,
  PRODUCT_URL,
  COUNTRY_URL,
  STATE_OR_PROVINCE_URL,
  TAX_CLASS_URL,
  TAX_RATE_URL,
  INVENTORY_WAREHOUSE_PRODUCTS_URL,
  WAREHOUSE_URL,
  INVENTORY_WAREHOUSE_STOCKS_URL,
  INVENTORY_WAREHOUSE_STOCKS_HISTORIES_URL,
  SALES_ORDERS_URL,
  SALES_SHIPMENTS_URL,
  SALES_RETURN_REQUESTS_URL,
  SALES_RECURRING_PAYMENTS_URL,
  SALES_GIFT_CARDS_URL,
  SALES_SHOPPING_CARTS_AND_WISHLISTS_URL,
  MANAGER_PROMOTIONS_URL,
  SYSTEM_PAYMENT_PROVIDERS,
  SYSTEM_SETTINGS,
  WEBHOOKS_URL,
  DEFAULT_PAGE_SIZE,
  DEFAULT_PAGE_NUMBER,
  FORMAT_DATE_YYYY_MM_DD_HH_MM,
  mappingExportingProductColumnNames,
} from './Common';

describe('Common constants', () => {
  describe('ToastVariant', () => {
    it('should have correct values', () => {
      expect(ToastVariant.SUCCESS).toBe('success');
      expect(ToastVariant.WARNING).toBe('warning');
      expect(ToastVariant.ERROR).toBe('error');
    });
  });

  describe('ResponseStatus', () => {
    it('should have correct HTTP status codes', () => {
      expect(ResponseStatus.CREATED).toBe(201);
      expect(ResponseStatus.SUCCESS).toBe(204);
      expect(ResponseStatus.NOT_FOUND).toBe(404);
      expect(ResponseStatus.BAD_REQUEST).toBe(400);
    });
  });

  describe('ResponseTitle', () => {
    it('should have correct titles', () => {
      expect(ResponseTitle.NOT_FOUND).toBe('Not Found');
      expect(ResponseTitle.BAD_REQUEST).toBe('Bad Request');
    });
  });

  describe('Message constants', () => {
    it('should have correct message strings', () => {
      expect(HAVE_BEEN_DELETED).toBe(' have been deleted');
      expect(DELETE_FAILED).toBe('Delete failed');
      expect(UPDATE_SUCCESSFULLY).toBe('Update successfully');
      expect(CREATE_SUCCESSFULLY).toBe('Create successfully');
      expect(UPDATE_FAILED).toBe('Update failed');
      expect(CREATE_FAILED).toBe('Create failed');
      expect(ADD_PRODUCT_INTO_WAREHOUSE_SUCCESSFULLY).toBe(
        'Add product(s) into warehouse successfully'
      );
    });
  });

  describe('Configuration constants', () => {
    it('should have correct toast duration', () => {
      expect(TOAST_DURATION).toBe(4000);
    });

    it('should have correct pagination defaults', () => {
      expect(DEFAULT_PAGE_SIZE).toBe(10);
      expect(DEFAULT_PAGE_NUMBER).toBe(0);
    });

    it('should have correct date format', () => {
      expect(FORMAT_DATE_YYYY_MM_DD_HH_MM).toBe('YYYYMMDDHHmmss');
    });
  });

  describe('URL constants', () => {
    it('should have correct URL paths', () => {
      expect(CATEGORIES_URL).toBe('/catalog/categories');
      expect(BRAND_URL).toBe('/catalog/brands');
      expect(CUSTOMER_URL).toBe('/customers');
      expect(PRODUCT_ATTRIBUTE_GROUPS_URL).toBe('/catalog/product-attribute-groups');
      expect(PRODUCT_OPTIONS_URL).toBe('/catalog/product-options');
      expect(PRODUCT_ATTRIBUTE_URL).toBe('/catalog/product-attributes');
      expect(PRODUCT_TEMPLATE_URL).toBe('/catalog/product-templates');
      expect(PRODUCT_URL).toBe('/catalog/products');
      expect(COUNTRY_URL).toBe('/location/countries');
      expect(STATE_OR_PROVINCE_URL).toBe('/location/state-or-provinces');
      expect(TAX_CLASS_URL).toBe('/tax/tax-classes');
      expect(TAX_RATE_URL).toBe('/tax/tax-rates');
      expect(INVENTORY_WAREHOUSE_PRODUCTS_URL).toBe('/inventory/warehouse-products');
      expect(WAREHOUSE_URL).toBe('/inventory/warehouses');
      expect(INVENTORY_WAREHOUSE_STOCKS_URL).toBe('/inventory/warehouse-stocks');
      expect(INVENTORY_WAREHOUSE_STOCKS_HISTORIES_URL).toBe(
        '/inventory/warehouse-stocks/histories'
      );
      expect(SALES_ORDERS_URL).toBe('/sales/orders');
      expect(SALES_SHIPMENTS_URL).toBe('/sales/shipments');
      expect(SALES_RETURN_REQUESTS_URL).toBe('/sales/return-requests');
      expect(SALES_RECURRING_PAYMENTS_URL).toBe('/sales/recurring-payments');
      expect(SALES_GIFT_CARDS_URL).toBe('/sales/gift-cards');
      expect(SALES_SHOPPING_CARTS_AND_WISHLISTS_URL).toBe(
        '/sales/shopping-carts-and-wishlists'
      );
      expect(MANAGER_PROMOTIONS_URL).toBe('/promotion/manager-promotion');
      expect(SYSTEM_PAYMENT_PROVIDERS).toBe('/system/payment-providers');
      expect(SYSTEM_SETTINGS).toBe('/system/settings');
      expect(WEBHOOKS_URL).toBe('/webhook');
    });
  });

  describe('mappingExportingProductColumnNames', () => {
    it('should have correct column mappings', () => {
      expect(mappingExportingProductColumnNames.id).toBe('Id');
      expect(mappingExportingProductColumnNames.name).toBe('Product Name');
      expect(mappingExportingProductColumnNames.shortDescription).toBe('Short Description');
      expect(mappingExportingProductColumnNames.description).toBe('Description');
      expect(mappingExportingProductColumnNames.specification).toBe('Specification');
      expect(mappingExportingProductColumnNames.sku).toBe('SKU');
      expect(mappingExportingProductColumnNames.gtin).toBe('GTIN');
      expect(mappingExportingProductColumnNames.slug).toBe('Slug');
      expect(mappingExportingProductColumnNames.isAllowedToOrder).toBe('Allowed Order');
      expect(mappingExportingProductColumnNames.isPublished).toBe('Published');
      expect(mappingExportingProductColumnNames.isFeatured).toBe('Featured');
      expect(mappingExportingProductColumnNames.isVisible).toBe('Visible');
      expect(mappingExportingProductColumnNames.stockTrackingEnabled).toBe(
        'Stock Tracking Enabled'
      );
      expect(mappingExportingProductColumnNames.price).toBe('Price');
      expect(mappingExportingProductColumnNames.brandId).toBe('Brand Id');
      expect(mappingExportingProductColumnNames.brandName).toBe('Brand Name');
      expect(mappingExportingProductColumnNames.metaTitle).toBe('Meta Title');
      expect(mappingExportingProductColumnNames.metaKeyword).toBe('Meta Keyword');
      expect(mappingExportingProductColumnNames.metaDescription).toBe('Meta Description');
    });
  });
});
