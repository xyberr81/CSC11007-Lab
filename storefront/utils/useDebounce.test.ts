import { renderHook, act } from '@testing-library/react';
import { useDebounce } from './useDebounce';

describe('useDebounce', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('should return initial value immediately', () => {
    const { result } = renderHook(() => useDebounce('hello', 500));
    expect(result.current).toBe('hello');
  });

  it('should not update value before delay', () => {
    const { result, rerender } = renderHook(
      ({ value, delay }: { value: string | undefined; delay: number }) => useDebounce(value, delay),
      { initialProps: { value: 'hello', delay: 500 } }
    );

    rerender({ value: 'world', delay: 500 });

    // Before delay expires
    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(result.current).toBe('hello');
  });

  it('should update value after delay', () => {
    const { result, rerender } = renderHook(
      ({ value, delay }: { value: string | undefined; delay: number }) => useDebounce(value, delay),
      { initialProps: { value: 'hello', delay: 500 } }
    );

    rerender({ value: 'world', delay: 500 });

    act(() => {
      jest.advanceTimersByTime(500);
    });

    expect(result.current).toBe('world');
  });

  it('should reset timer on rapid value changes', () => {
    const { result, rerender } = renderHook(
      ({ value, delay }: { value: string | undefined; delay: number }) => useDebounce(value, delay),
      { initialProps: { value: 'a', delay: 500 } }
    );

    rerender({ value: 'ab', delay: 500 });
    act(() => {
      jest.advanceTimersByTime(300);
    });

    rerender({ value: 'abc', delay: 500 });
    act(() => {
      jest.advanceTimersByTime(300);
    });

    // 'ab' should NOT have taken effect because timer was reset
    expect(result.current).toBe('a');

    act(() => {
      jest.advanceTimersByTime(200);
    });

    // Now 500ms after last change ('abc'), it should update
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
