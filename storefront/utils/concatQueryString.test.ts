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
    const result = concatQueryString(
      ['page=1', 'size=10', 'sort=name'],
      '/api/products'
    );
    expect(result).toBe('/api/products?page=1&size=10&sort=name');
  });

  it('should work with empty string url', () => {
    const result = concatQueryString(['key=value'], '');
    expect(result).toBe('?key=value');
  });

  it('should preserve url that already has path', () => {
    const result = concatQueryString(
      ['category=electronics'],
      'http://localhost:3000/api/search'
    );
    expect(result).toBe(
      'http://localhost:3000/api/search?category=electronics'
    );
  });
});
