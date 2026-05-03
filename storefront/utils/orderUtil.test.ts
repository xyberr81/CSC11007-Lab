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
