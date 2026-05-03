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
