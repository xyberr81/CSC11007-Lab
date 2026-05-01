package com.yas.customer.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void constructor_WhenThreeArgs_InitializesCorrectly() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "Detail");
        assertThat(vm.statusCode()).isEqualTo("400");
        assertThat(vm.title()).isEqualTo("Bad Request");
        assertThat(vm.detail()).isEqualTo("Detail");
        assertThat(vm.fieldErrors()).isEmpty();
    }

    @Test
    void constructor_WhenFourArgs_InitializesCorrectly() {
        List<String> fieldErrors = List.of("error1");
        ErrorVm vm = new ErrorVm("400", "Bad Request", "Detail", fieldErrors);
        assertThat(vm.fieldErrors()).isEqualTo(fieldErrors);
    }
}
