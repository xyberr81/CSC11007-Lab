import { formatPriceVND, formatPriceUSD } from './formatPrice';

describe('formatPriceVND', () => {
  it('should format integer price in VND', () => {
    const result = formatPriceVND(100000);
    expect(result).toContain('100.000');
  });

  it('should format zero price', () => {
    const result = formatPriceVND(0);
    expect(result).toContain('0');
  });

  it('should format large price in VND', () => {
    const result = formatPriceVND(1234567);
    expect(result).toContain('1.234.567');
  });

  it('should format negative price in VND', () => {
    const result = formatPriceVND(-50000);
    expect(result).toContain('50.000');
  });

  it('should return a string containing VND currency symbol', () => {
    const result = formatPriceVND(1000);
    expect(result).toContain('₫');
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
