import { toast } from 'react-toastify';
import { toastSuccess, toastError } from './ToastService';

jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe('ToastService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('toastSuccess', () => {
    it('should call toast.success with message and default options', () => {
      toastSuccess('Operation successful');

      expect(toast.success).toHaveBeenCalledWith(
        'Operation successful',
        expect.objectContaining({
          position: 'top-right',
          autoClose: 3000,
          closeOnClick: true,
          pauseOnHover: false,
          theme: 'colored',
        })
      );
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

      expect(toast.error).toHaveBeenCalledWith(
        'Something went wrong',
        expect.objectContaining({
          position: 'top-right',
          autoClose: 3000,
          closeOnClick: true,
          pauseOnHover: false,
          theme: 'colored',
        })
      );
    });

    it('should call toast.error with custom options when provided', () => {
      const customOptions = { position: 'bottom-left' as const };
      toastError('Error occurred', customOptions);

      expect(toast.error).toHaveBeenCalledWith('Error occurred', customOptions);
    });
  });
});
