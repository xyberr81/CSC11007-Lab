# Hướng dẫn: Viết và chạy Unit Test cho storefront-bff & backoffice-bff

---

## A. STOREFRONT-BFF

### Cấu trúc source code

```
storefront-bff/src/main/java/com/yas/storefrontbff/
├── config/
│   ├── SecurityConfig.java       ← EXCLUDE khỏi jacoco
│   └── ServiceUrlConfig.java     ← EXCLUDE khỏi jacoco
├── controller/
│   └── AuthenticationController.java
├── viewmodel/
│   ├── AuthenticatedUserVm.java
│   ├── AuthenticationInfoVm.java
│   ├── CartDetailVm.java
│   ├── CartGetDetailVm.java
│   ├── CartItemVm.java
│   ├── GuestUserVm.java
│   └── TokenResponseVm.java
└── StorefrontBffApplication.java  ← EXCLUDE khỏi jacoco
```

> ℹ️ `config/` và `*Application.java` đã được exclude sẵn trong cấu hình JaCoCo của parent `pom.xml` — không cần viết test cho các class này. Tuy nhiên, vẫn cần phải kích hoạt JaCoCo ở phase test để CI ghi nhận coverage.

---

### Bước 1: Thêm JaCoCo Execution vào `pom.xml`

Mở file `storefront-bff/pom.xml`, tìm thẻ `<plugins>` và thêm cấu hình `jacoco-maven-plugin` (nếu chưa có):

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.14</version>
    <executions>
        <execution>
            <id>default</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

---

### Bước 2: Tạo thư mục test

```powershell
cd D:\...\yas\storefront-bff

New-Item -ItemType Directory -Path src\test\java\com\yas\storefrontbff\viewmodel -Force
New-Item -ItemType Directory -Path src\test\java\com\yas\storefrontbff\controller -Force
```

---

### Bước 3: Viết test cho tất cả ViewModel

#### `AuthenticatedUserVmTest.java`

Tạo file `New-Item src\test\java\com\yas\storefrontbff\viewmodel\AuthenticatedUserVmTest.java`

```java
package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserVmTest {

    @Test
    void testCreateWithUsername() {
        var vm = new AuthenticatedUserVm("john_doe");
        assertEquals("john_doe", vm.username());
    }

    @Test
    void testCreateWithNullUsername() {
        var vm = new AuthenticatedUserVm(null);
        assertNull(vm.username());
    }

    @Test
    void testEquality() {
        var vm1 = new AuthenticatedUserVm("john");
        var vm2 = new AuthenticatedUserVm("john");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new AuthenticatedUserVm("john");
        var vm2 = new AuthenticatedUserVm("jane");
        assertNotEquals(vm1, vm2);
    }

    @Test
    void testToString() {
        var vm = new AuthenticatedUserVm("john_doe");
        assertTrue(vm.toString().contains("john_doe"));
    }
}
```

#### `AuthenticationInfoVmTest.java`

Tạo file `New-Item src\test\java\com\yas\storefrontbff\viewmodel\AuthenticationInfoVmTest.java`

```java
package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationInfoVmTest {

    @Test
    void testAuthenticatedWithUser() {
        var user = new AuthenticatedUserVm("john");
        var vm = new AuthenticationInfoVm(true, user);
        assertTrue(vm.isAuthenticated());
        assertEquals(user, vm.authenticatedUser());
    }

    @Test
    void testUnauthenticatedWithNullUser() {
        var vm = new AuthenticationInfoVm(false, null);
        assertFalse(vm.isAuthenticated());
        assertNull(vm.authenticatedUser());
    }

    @Test
    void testEquality() {
        var user = new AuthenticatedUserVm("john");
        var vm1 = new AuthenticationInfoVm(true, user);
        var vm2 = new AuthenticationInfoVm(true, user);
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new AuthenticationInfoVm(true, new AuthenticatedUserVm("john"));
        var vm2 = new AuthenticationInfoVm(false, null);
        assertNotEquals(vm1, vm2);
    }
}
```

#### `CartDetailVmTest.java`

Tạo file `New-Item src\test\java\com\yas\storefrontbff\viewmodel\CartDetailVmTest.java`

```java
package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartDetailVmTest {

    @Test
    void testCreateCartDetail() {
        var vm = new CartDetailVm(1L, 100L, 3);
        assertEquals(1L, vm.id());
        assertEquals(100L, vm.productId());
        assertEquals(3, vm.quantity());
    }

    @Test
    void testEquality() {
        var vm1 = new CartDetailVm(1L, 100L, 3);
        var vm2 = new CartDetailVm(1L, 100L, 3);
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new CartDetailVm(1L, 100L, 3);
        var vm2 = new CartDetailVm(2L, 200L, 5);
        assertNotEquals(vm1, vm2);
    }

    @Test
    void testNullId() {
        var vm = new CartDetailVm(null, 100L, 1);
        assertNull(vm.id());
    }
}
```

#### `CartGetDetailVmTest.java`

Tạo file `New-Item src\test\java\com\yas\storefrontbff\viewmodel\CartGetDetailVmTest.java`

```java
package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CartGetDetailVmTest {

    @Test
    void testCreateWithCartDetails() {
        var details = List.of(new CartDetailVm(1L, 100L, 2));
        var vm = new CartGetDetailVm(10L, "customer-01", details);
        assertEquals(10L, vm.id());
        assertEquals("customer-01", vm.customerId());
        assertEquals(1, vm.cartDetails().size());
    }

    @Test
    void testCreateWithEmptyCartDetails() {
        var vm = new CartGetDetailVm(1L, "customer-01", List.of());
        assertTrue(vm.cartDetails().isEmpty());
    }

    @Test
    void testCreateWithNullCustomerId() {
        var vm = new CartGetDetailVm(1L, null, List.of());
        assertNull(vm.customerId());
    }

    @Test
    void testEquality() {
        var details = List.of(new CartDetailVm(1L, 100L, 2));
        var vm1 = new CartGetDetailVm(1L, "customer-01", details);
        var vm2 = new CartGetDetailVm(1L, "customer-01", details);
        assertEquals(vm1, vm2);
    }
}
```

#### `CartItemVmTest.java`

Tạo file `New-Item src\test\java\com\yas\storefrontbff\viewmodel\CartItemVmTest.java`

```java
package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemVmTest {

    @Test
    void testCreateCartItem() {
        var vm = new CartItemVm(100L, 5);
        assertEquals(100L, vm.productId());
        assertEquals(5, vm.quantity());
    }

    @Test
    void testFromCartDetailVm() {
        var cartDetail = new CartDetailVm(1L, 200L, 3);
        var vm = CartItemVm.fromCartDetailVm(cartDetail);
        assertEquals(200L, vm.productId());
        assertEquals(3, vm.quantity());
    }

    @Test
    void testFromCartDetailVm_preservesQuantity() {
        var cartDetail = new CartDetailVm(5L, 999L, 10);
        var vm = CartItemVm.fromCartDetailVm(cartDetail);
        assertEquals(10, vm.quantity());
    }

    @Test
    void testEquality() {
        var vm1 = new CartItemVm(100L, 5);
        var vm2 = new CartItemVm(100L, 5);
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new CartItemVm(100L, 5);
        var vm2 = new CartItemVm(200L, 3);
        assertNotEquals(vm1, vm2);
    }
}
```

#### `GuestUserVmTest.java`

Tạo file `New-Item src\test\java\com\yas\storefrontbff\viewmodel\GuestUserVmTest.java`

```java
package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GuestUserVmTest {

    @Test
    void testCreateGuestUser() {
        var vm = new GuestUserVm("user-001", "guest@email.com", "secret123");
        assertEquals("user-001", vm.userId());
        assertEquals("guest@email.com", vm.email());
        assertEquals("secret123", vm.password());
    }

    @Test
    void testCreateWithNulls() {
        var vm = new GuestUserVm(null, null, null);
        assertNull(vm.userId());
        assertNull(vm.email());
        assertNull(vm.password());
    }

    @Test
    void testEquality() {
        var vm1 = new GuestUserVm("u1", "a@b.com", "pass");
        var vm2 = new GuestUserVm("u1", "a@b.com", "pass");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new GuestUserVm("u1", "a@b.com", "pass1");
        var vm2 = new GuestUserVm("u2", "b@c.com", "pass2");
        assertNotEquals(vm1, vm2);
    }
}
```

#### `TokenResponseVmTest.java`

Tạo file `New-Item src\test\java\com\yas\storefrontbff\viewmodel\TokenResponseVmTest.java`

```java
package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenResponseVmTest {

    @Test
    void testCreateTokenResponse() {
        var vm = new TokenResponseVm("access-token-xyz", "refresh-token-abc");
        assertEquals("access-token-xyz", vm.accessToken());
        assertEquals("refresh-token-abc", vm.refreshToken());
    }

    @Test
    void testCreateWithNullTokens() {
        var vm = new TokenResponseVm(null, null);
        assertNull(vm.accessToken());
        assertNull(vm.refreshToken());
    }

    @Test
    void testEquality() {
        var vm1 = new TokenResponseVm("acc", "ref");
        var vm2 = new TokenResponseVm("acc", "ref");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new TokenResponseVm("acc1", "ref1");
        var vm2 = new TokenResponseVm("acc2", "ref2");
        assertNotEquals(vm1, vm2);
    }
}
```

---

### Bước 4: Viết test cho AuthenticationController

Tạo file `New-Item src\test\java\com\yas\storefrontbff\controller\AuthenticationControllerTest.java`

```java
package com.yas.storefrontbff.controller;

import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void whenPrincipalIsNull_thenReturnUnauthenticated() {
        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAuthenticated());
        assertNull(response.getBody().authenticatedUser());
    }

    @Test
    void whenPrincipalIsValid_thenReturnAuthenticatedUser() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn("john_doe");

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(mockPrincipal);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAuthenticated());
        assertNotNull(response.getBody().authenticatedUser());
        assertEquals("john_doe", response.getBody().authenticatedUser().username());
    }

    @Test
    void whenPrincipalHasNullUsername_thenReturnAuthenticatedWithNullUsername() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(mockPrincipal);

        assertTrue(response.getBody().isAuthenticated());
        assertNull(response.getBody().authenticatedUser().username());
    }
}
```

---

### Bước 5: Chạy test và xem coverage

```powershell
./mvnw test jacoco:report
Invoke-Item target\site\jacoco\index.html
```

**Kết quả đạt được: 100% coverage** ✅

---

---

## B. BACKOFFICE-BFF

### Cấu trúc source code

```
backoffice-bff/src/main/java/com/yas/backofficebff/
├── config/
│   └── SecurityConfig.java       ← EXCLUDE khỏi jacoco
├── controller/
│   └── AuthenticationController.java
├── viewmodel/
│   └── AuthenticatedUser.java
└── Application.java               ← EXCLUDE khỏi jacoco
```

---

### Điểm khác biệt so với storefront-bff

|                                   | storefront-bff                       | backoffice-bff                           |
| --------------------------------- | ------------------------------------ | ---------------------------------------- |
| Controller xử lý `null` principal | Có — trả về `isAuthenticated: false` | **Không** — throw `NullPointerException` |
| Số ViewModel                      | 7 class                              | 1 class                                  |
| JaCoCo trong pom.xml              | Có sẵn                               | **Phải tự thêm**                         |

---

### Bước 1: Thêm JaCoCo vào pom.xml

> ⚠️ backoffice-bff **không có sẵn JaCoCo** trong `pom.xml`. Phải thêm thủ công vào phần `<build><plugins>`.

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.14</version>
    <executions>
        <execution>
            <id>default</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

### Bước 2: Tạo thư mục test

```powershell
cd D:\...\yas\backoffice-bff

New-Item -ItemType Directory -Path src\test\java\com\yas\backofficebff\viewmodel -Force
New-Item -ItemType Directory -Path src\test\java\com\yas\backofficebff\controller -Force
```

---

### Bước 3: Viết test cho AuthenticatedUser (viewmodel)

Tạo file `New-Item src\test\java\com\yas\backofficebff\viewmodel\AuthenticatedUserTest.java`

```java
package com.yas.backofficebff.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserTest {

    @Test
    void testCreateWithUsername() {
        var vm = new AuthenticatedUser("admin_user");
        assertEquals("admin_user", vm.username());
    }

    @Test
    void testCreateWithNullUsername() {
        var vm = new AuthenticatedUser(null);
        assertNull(vm.username());
    }

    @Test
    void testEquality() {
        var vm1 = new AuthenticatedUser("admin");
        var vm2 = new AuthenticatedUser("admin");
        assertEquals(vm1, vm2);
    }

    @Test
    void testInequality() {
        var vm1 = new AuthenticatedUser("admin");
        var vm2 = new AuthenticatedUser("user");
        assertNotEquals(vm1, vm2);
    }

    @Test
    void testToString() {
        var vm = new AuthenticatedUser("admin_user");
        assertTrue(vm.toString().contains("admin_user"));
    }

    @Test
    void testHashCode() {
        var vm1 = new AuthenticatedUser("admin");
        var vm2 = new AuthenticatedUser("admin");
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }
}
```

---

### Bước 4: Viết test cho AuthenticationController

Tạo file `New-Item src\test\java\com\yas\backofficebff\controller\AuthenticationControllerTest.java`

> ⚠️ Controller này **không có null check** — nếu principal là null sẽ throw `NullPointerException`. Cần test cả trường hợp này.

```java
package com.yas.backofficebff.controller;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void whenPrincipalIsValid_thenReturnAuthenticatedUser() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn("admin_user");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(mockPrincipal);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("admin_user", response.getBody().username());
    }

    @Test
    void whenPrincipalHasNullUsername_thenReturnUserWithNullUsername() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(mockPrincipal);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody().username());
    }

    @Test
    void whenPrincipalIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            authenticationController.user(null);
        });
    }

    @Test
    void whenPrincipalHasDifferentUsername_thenReturnCorrectUsername() {
        OAuth2User mockPrincipal = mock(OAuth2User.class);
        when(mockPrincipal.getAttribute("preferred_username")).thenReturn("another_admin");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(mockPrincipal);

        assertEquals("another_admin", response.getBody().username());
    }
}
```

---

### Bước 5: Chạy test và xem coverage

```powershell
./mvnw test jacoco:report
Invoke-Item target\site\jacoco\index.html
```

**Kết quả đạt được: 100% coverage** ✅

---

---

## Tổng kết

| Service          | Tests    | Coverage    |
| ---------------- | -------- | ----------- |
| `storefront-bff` | 13 tests | **100%** ✅ |
| `backoffice-bff` | 10 tests | **100%** ✅ |

### Lỗi đã gặp và cách fix

| Lỗi                                                            | Nguyên nhân                                         | Cách fix                                                         |
| -------------------------------------------------------------- | --------------------------------------------------- | ---------------------------------------------------------------- |
| `Skipping JaCoCo execution due to missing execution data file` | backoffice-bff không có JaCoCo trong pom.xml        | Thêm jacoco-maven-plugin vào `<build><plugins>`                  |
| `Cannot find path target\site\jacoco\index.html`               | Dùng `start` thay vì `Invoke-Item` trong PowerShell | Dùng `Invoke-Item target\site\jacoco\index.html`                 |
| `Unknown lifecycle phase .run.arguments=...`                   | PowerShell xử lý dấu `=` khác bash                  | Bọc argument trong nháy kép: `"-Dspring-boot.run.arguments=..."` |
