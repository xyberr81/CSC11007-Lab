package com.yas.customer.viewmodel.useraddress;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.customer.model.UserAddress;
import com.yas.customer.viewmodel.address.AddressVm;
import org.junit.jupiter.api.Test;

class UserAddressVmTest {

    @Test
    void fromModel_WhenNormalCase_ReturnsVm() {
        UserAddress userAddress = UserAddress.builder()
            .userId("user1")
            .isActive(true)
            .addressId(1L)
            .build();
        AddressVm addressVm = AddressVm.builder().id(1L).build();

        UserAddressVm vm = UserAddressVm.fromModel(userAddress, addressVm);

        assertThat(vm.userId()).isEqualTo("user1");
        assertThat(vm.isActive()).isTrue();
        assertThat(vm.addressGetVm()).isEqualTo(addressVm);
    }
}
