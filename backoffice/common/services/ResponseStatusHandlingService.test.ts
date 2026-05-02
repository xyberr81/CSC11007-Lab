import {
  handleDeletingResponse,
  handleUpdatingResponse,
  handleCreatingResponse,
  handleResponse,
} from './ResponseStatusHandlingService';
import { toastSuccess, toastError } from './ToastService';

jest.mock('./ToastService', () => ({
  toastSuccess: jest.fn(),
  toastError: jest.fn(),
}));

describe('ResponseStatusHandlingService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  // ─── handleDeletingResponse ──────────────────────────────────
  describe('handleDeletingResponse', () => {
    it('should show success toast when status is 204', () => {
      handleDeletingResponse({ status: 204 }, 'Product');
      expect(toastSuccess).toHaveBeenCalledWith('Product have been deleted');
    });

    it('should show error detail when title is Not Found', () => {
      handleDeletingResponse(
        { title: 'Not Found', detail: 'Item not found' },
        'Product'
      );
      expect(toastError).toHaveBeenCalledWith('Item not found');
    });

    it('should show error detail when title is Bad Request', () => {
      handleDeletingResponse(
        { title: 'Bad Request', detail: 'Invalid ID' },
        'Product'
      );
      expect(toastError).toHaveBeenCalledWith('Invalid ID');
    });

    it('should show generic delete failed for other errors', () => {
      handleDeletingResponse({ status: 500 }, 'Product');
      expect(toastError).toHaveBeenCalledWith('Delete failed');
    });
  });

  // ─── handleUpdatingResponse ──────────────────────────────────
  describe('handleUpdatingResponse', () => {
    it('should show success toast when status is 204', () => {
      handleUpdatingResponse({ status: 204 });
      expect(toastSuccess).toHaveBeenCalledWith('Update successfully');
    });

    it('should show error detail when title is Bad Request', () => {
      handleUpdatingResponse({
        title: 'Bad Request',
        detail: 'Validation error',
      });
      expect(toastError).toHaveBeenCalledWith('Validation error');
    });

    it('should show error detail when title is Not Found', () => {
      handleUpdatingResponse({
        title: 'Not Found',
        detail: 'Resource not found',
      });
      expect(toastError).toHaveBeenCalledWith('Resource not found');
    });

    it('should show generic update failed for other errors', () => {
      handleUpdatingResponse({ status: 500 });
      expect(toastError).toHaveBeenCalledWith('Update failed');
    });
  });

  // ─── handleCreatingResponse ──────────────────────────────────
  describe('handleCreatingResponse', () => {
    it('should show success toast when status is 201', async () => {
      await handleCreatingResponse({ status: 201 });
      expect(toastSuccess).toHaveBeenCalledWith('Create successfully');
    });

    it('should show error detail when status is 400', async () => {
      const mockResponse = {
        status: 400,
        json: jest.fn().mockResolvedValue({ detail: 'Name is required' }),
      };
      await handleCreatingResponse(mockResponse);
      expect(toastError).toHaveBeenCalledWith('Name is required');
    });

    it('should show generic create failed for other errors', async () => {
      await handleCreatingResponse({ status: 500 });
      expect(toastError).toHaveBeenCalledWith('Create failed');
    });

    it('should handle error when json() parsing fails', async () => {
      const mockResponse = {
        status: 400,
        json: jest.fn().mockRejectedValue(new Error('Invalid JSON')),
      };

      await expect(handleCreatingResponse(mockResponse as any))
        .rejects.toThrow('Invalid JSON');
    });
  });

  // ─── handleResponse ──────────────────────────────────────────
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
