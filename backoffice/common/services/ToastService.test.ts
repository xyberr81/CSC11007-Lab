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
