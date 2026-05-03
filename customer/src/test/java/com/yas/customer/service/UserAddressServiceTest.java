package com.yas.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.util.SecurityContextUtils;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

class UserAddressServiceTest {

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    private static final String USER_ID = "test-user";
    private static final String ANONYMOUS_USER = "anonymousUser";

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
        SecurityContextUtils.setUpSecurityContext(USER_ID);
    }

    @Test
    void getUserAddressList_WhenAnonymousUser_ThrowsAccessDeniedException() {
        SecurityContextUtils.setUpSecurityContext(ANONYMOUS_USER);
        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void getUserAddressList_WhenNormalCase_ReturnsSortedList() {
        UserAddress userAddress1 = UserAddress.builder().userId(USER_ID).addressId(1L).isActive(false).build();
        UserAddress userAddress2 = UserAddress.builder().userId(USER_ID).addressId(2L).isActive(true).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(userAddress1, userAddress2));

        AddressDetailVm address1 = AddressDetailVm.builder().id(1L).build();
        AddressDetailVm address2 = AddressDetailVm.builder().id(2L).build();
        when(locationService.getAddressesByIdList(anyList())).thenReturn(List.of(address1, address2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(0).id()).isEqualTo(2L);
        assertThat(result.get(1).isActive()).isFalse();
        assertThat(result.get(1).id()).isEqualTo(1L);
    }

    @Test
    void getAddressDefault_WhenAnonymousUser_ThrowsAccessDeniedException() {
        SecurityContextUtils.setUpSecurityContext(ANONYMOUS_USER);
        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_WhenNoDefaultAddress_ThrowsNotFoundException() {
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void getAddressDefault_WhenNormalCase_ReturnsAddress() {
        UserAddress userAddress = UserAddress.builder().userId(USER_ID).addressId(1L).isActive(true).build();
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(userAddress));
        AddressDetailVm address = AddressDetailVm.builder().id(1L).build();
        when(locationService.getAddressById(1L)).thenReturn(address);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isEqualTo(address);
    }

    @Test
    void createAddress_WhenFirstAddress_SetsActiveTrue() {
        AddressPostVm postVm = new AddressPostVm("name", "phone", "line1", "city", "zip", 1L, 1L, 1L);
        AddressVm addressVm = AddressVm.builder().id(1L).build();
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(Collections.emptyList());
        when(userAddressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.isActive()).isTrue();
        verify(userAddressRepository).save(any(UserAddress.class));
    }

    @Test
    void createAddress_WhenNotFirstAddress_SetsActiveFalse() {
        AddressPostVm postVm = new AddressPostVm("name", "phone", "line1", "city", "zip", 1L, 1L, 1L);
        AddressVm addressVm = AddressVm.builder().id(2L).build();
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(new UserAddress()));
        when(userAddressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void deleteAddress_WhenAddressNotFound_ThrowsNotFoundException() {
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(1L));
    }

    @Test
    void deleteAddress_WhenNormalCase_DeletesAddress() {
        UserAddress userAddress = UserAddress.builder().userId(USER_ID).addressId(1L).build();
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, 1L)).thenReturn(userAddress);

        userAddressService.deleteAddress(1L);

        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void chooseDefaultAddress_WhenNormalCase_UpdatesActiveStatus() {
        UserAddress userAddress1 = UserAddress.builder().userId(USER_ID).addressId(1L).isActive(true).build();
        UserAddress userAddress2 = UserAddress.builder().userId(USER_ID).addressId(2L).isActive(false).build();
        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(userAddress1, userAddress2));

        userAddressService.chooseDefaultAddress(2L);

        assertThat(userAddress1.getIsActive()).isFalse();
        assertThat(userAddress2.getIsActive()).isTrue();
        verify(userAddressRepository).saveAll(anyList());
    }
}
