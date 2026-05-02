# Báo cáo Giai đoạn 2 - Thành viên 2 (Integrator & Quality Gate)

Tài liệu này tổng hợp **toàn bộ công việc đã thực hiện** trong Giai đoạn 2, bao gồm: viết Unit Test, cấu hình Quality Gate, thiết lập CI Pipeline, và hướng dẫn chạy kiểm thử local.

---

## 1. Tổng quan công việc đã hoàn thành

### ✅ Checklist hoàn thành

| Hạng mục | Trạng thái | Chi tiết |
| --- | --- | --- |
| Unit Test — `storefront` (Frontend/Next.js) | ✅ Hoàn thành | 39 tests, 100% coverage |
| Unit Test — `backoffice` (Frontend/Next.js) | ✅ Hoàn thành | 43 tests, 100% coverage |
| Unit Test — `storefront-bff` (Backend/Java) | ✅ Hoàn thành | 33 tests, 100% coverage |
| Unit Test — `backoffice-bff` (Backend/Java) | ✅ Hoàn thành | 10 tests, 100% coverage |
| Cấu hình JaCoCo cho Java services | ✅ Hoàn thành | `storefront-bff`, `backoffice-bff` |
| Cấu hình Jest `coverageReporters` cho Frontend | ✅ Hoàn thành | Thêm `['json-summary', 'text', 'lcov']` |
| Script Quality Gate — Bash (cho CI/GitHub Actions) | ✅ Hoàn thành | `scripts/check_quality_gate.sh` |
| Script Quality Gate — PowerShell (cho local Windows) | ✅ Hoàn thành | `scripts/check_quality_gate.ps1` |
| CI Pipeline — GitHub Actions | ✅ Hoàn thành | `.github/workflows/ci.yml` |

---

## 2. Chi tiết Unit Test đã viết

### A. `storefront` (Frontend — Next.js)

**Tài liệu chi tiết:** [`docs/phase1/unit-test-storefront-guide.md`](../phase1/unit-test-storefront-guide.md)

| File test | File source | Số tests | Coverage |
| --- | --- | --- | --- |
| `utils/concatQueryString.test.ts` | `utils/concatQueryString.ts` | 6 | 100% |
| `utils/formatPrice.test.ts` | `utils/formatPrice.ts` | 7 | 100% |
| `utils/orderUtil.test.ts` | `utils/orderUtil.ts` | 20 | 100% |
| `utils/useDebounce.test.ts` | `utils/useDebounce.ts` | 6 | 100% |
| **Tổng** | | **39** | **100%** |

---

### B. `backoffice` (Frontend — Next.js)

**Tài liệu chi tiết:** [`docs/phase1/unit-test-backoffice-guide.md`](../phase1/unit-test-backoffice-guide.md)

| File test | File source | Số tests | Coverage |
| --- | --- | --- | --- |
| `utils/concatQueryString.test.ts` | `utils/concatQueryString.ts` | 6 | 100% |
| `utils/formatPrice.test.ts` | `utils/formatPrice.ts` | 11 | 100% |
| `common/services/ToastService.test.ts` | `common/services/ToastService.ts` | 4 | 100% |
| `common/services/ResponseStatusHandlingService.test.ts` | `common/services/ResponseStatusHandlingService.ts` | 13 | 100% |
| `constants/Common.test.ts` | `constants/Common.ts` | 9 | 100% |
| **Tổng** | | **43** | **100%** |

---

### C. `storefront-bff` (Backend — Java/Spring Boot)

**Tài liệu chi tiết:** [`docs/phase1/unit-test-bff-guide.md`](../phase1/unit-test-bff-guide.md) (phần A)

**Cấu trúc source code:**
```
storefront-bff/src/main/java/com/yas/storefrontbff/
├── config/
│   ├── SecurityConfig.java         ← EXCLUDE (JaCoCo)
│   └── ServiceUrlConfig.java       ← EXCLUDE (JaCoCo)
├── controller/
│   └── AuthenticationController.java   ← Có test ✅
├── viewmodel/
│   ├── AuthenticatedUserVm.java        ← Có test ✅
│   ├── AuthenticationInfoVm.java       ← Có test ✅
│   ├── CartDetailVm.java              ← Có test ✅
│   ├── CartGetDetailVm.java           ← Có test ✅
│   ├── CartItemVm.java               ← Có test ✅
│   ├── GuestUserVm.java              ← Có test ✅
│   └── TokenResponseVm.java          ← Có test ✅
└── StorefrontBffApplication.java    ← EXCLUDE (JaCoCo)
```

| File test | File source | Số tests | Coverage |
| --- | --- | --- | --- |
| `AuthenticatedUserVmTest.java` | `AuthenticatedUserVm.java` | 5 | 100% |
| `AuthenticationInfoVmTest.java` | `AuthenticationInfoVm.java` | 4 | 100% |
| `CartDetailVmTest.java` | `CartDetailVm.java` | 4 | 100% |
| `CartGetDetailVmTest.java` | `CartGetDetailVm.java` | 4 | 100% |
| `CartItemVmTest.java` | `CartItemVm.java` | 5 | 100% |
| `GuestUserVmTest.java` | `GuestUserVm.java` | 4 | 100% |
| `TokenResponseVmTest.java` | `TokenResponseVm.java` | 4 | 100% |
| `AuthenticationControllerTest.java` | `AuthenticationController.java` | 3 | 100% |
| **Tổng** | | **33** | **100%** |

> **Ghi chú:** `config/` và `*Application.java` đã được exclude trong cấu hình JaCoCo (kế thừa từ parent `pom.xml`) nên không cần viết test.

---

### D. `backoffice-bff` (Backend — Java/Spring Boot)

**Tài liệu chi tiết:** [`docs/phase1/unit-test-bff-guide.md`](../phase1/unit-test-bff-guide.md) (phần B)

**Cấu trúc source code:**
```
backoffice-bff/src/main/java/com/yas/backofficebff/
├── config/
│   └── SecurityConfig.java           ← EXCLUDE (JaCoCo)
├── controller/
│   └── AuthenticationController.java     ← Có test ✅
├── viewmodel/
│   └── AuthenticatedUser.java            ← Có test ✅
└── Application.java                   ← EXCLUDE (JaCoCo)
```

| File test | File source | Số tests | Coverage |
| --- | --- | --- | --- |
| `AuthenticatedUserTest.java` | `AuthenticatedUser.java` | 6 | 100% |
| `AuthenticationControllerTest.java` | `AuthenticationController.java` | 4 | 100% |
| **Tổng** | | **10** | **100%** |

> **Ghi chú:** `backoffice-bff` ban đầu **không có JaCoCo** trong `pom.xml` — đã thêm thủ công plugin `jacoco-maven-plugin`.

---

## 3. Cấu hình Quality Gate

### A. Script Bash — `scripts/check_quality_gate.sh` (dùng trên CI/GitHub Actions)

- **Input:** Tên service (ví dụ: `product`, `storefront`, `storefront-bff`)
- **Logic:**
  - Kiểm tra `pom.xml` tồn tại → **Java service** → đọc `target/site/jacoco/jacoco.csv` → tính coverage từ cột `INSTRUCTION_MISSED` và `INSTRUCTION_COVERED`
  - Kiểm tra `package.json` tồn tại → **Node.js service** → đọc `coverage/coverage-summary.json` → lấy `total.lines.pct`
- **Ngưỡng:** Coverage **> 70%** → PASSED, ngược lại → FAILED (exit code 1)

### B. Script PowerShell — `scripts/check_quality_gate.ps1` (dùng trên local Windows)

- Logic tương tự bash script nhưng viết bằng PowerShell syntax
- Sử dụng `Import-Csv` (cho Java) và `ConvertFrom-Json` (cho Node.js)
- **Cách gọi:** `.\scripts\check_quality_gate.ps1 -Service <tên_service>`

### C. Cấu hình Frontend `jest.config.js`

Đã thêm `coverageReporters` để script có thể đọc báo cáo JSON:
```javascript
coverageReporters: ['json-summary', 'text', 'lcov'],
```
Áp dụng cho cả `storefront/jest.config.js` và `backoffice/jest.config.js`.

---

## 4. CI Pipeline — GitHub Actions

**File:** `.github/workflows/ci.yml`

### Kiến trúc Pipeline

```
┌──────────────────┐     ┌──────────┐     ┌──────────┐     ┌─────────────┐
│ detect-changes   │ ──▶ │   test   │ ──▶ │  build   │ ──▶ │ ci-success  │
│ (phát hiện thay  │     │ (chạy    │     │ (đóng    │     │ (kiểm tra   │
│  đổi service)    │     │  test +  │     │  gói)    │     │  tổng kết)  │
│                  │     │  QG)     │     │          │     │             │
└──────────────────┘     └──────────┘     └──────────┘     └─────────────┘
```

### Các service được hỗ trợ

Pipeline phát hiện thay đổi tự động cho **21 services**:
- **Backend (Java):** `product`, `cart`, `customer`, `order`, `inventory`, `payment`, `payment-paypal`, `rating`, `search`, `media`, `location`, `tax`, `promotion`, `webhook`, `sampledata`, `recommendation`, `delivery`
- **BFF (Java):** `storefront-bff`, `backoffice-bff`
- **Frontend (Node.js):** `storefront`, `backoffice`

### Job: `test`
1. Phát hiện loại project (Java/Node.js) dựa vào `pom.xml`/`package.json`
2. Chạy test:
   - Java: `mvn clean test -pl <service> -am`
   - Node.js: `npm ci && npm run test:coverage`
3. Chạy Quality Gate: `./scripts/check_quality_gate.sh <service>`
4. Upload artifacts: test results + coverage report

### Job: `build`
- Java: `mvn clean package -DskipTests -pl <service> -am`
- Node.js: `npm ci && npm run build`

### Job: `ci-success`
- Kiểm tra kết quả tổng hợp → quyết định CI pass/fail

---

## 5. Hướng dẫn chạy kiểm thử trên máy cá nhân (Windows PowerShell)

> **Yêu cầu:** Tất cả lệnh đều chạy từ thư mục gốc của project:
> `d:\...\CSC11007-Lab01`

---

### A. Frontend — `storefront`

```powershell
# 1. Di chuyển vào thư mục storefront
cd storefront

# 2. Cài đặt dependencies (nếu chưa có node_modules)
npm install

# 3. Chạy test + coverage
npm run test:coverage

# 4. Quay lại thư mục gốc
cd ..

# 5. Kiểm tra Quality Gate
.\scripts\check_quality_gate.ps1 -Service storefront
```

**Kết quả mong đợi:**
```
Coverage for storefront is 100%
Quality Gate PASSED: Coverage is > 70%
```

---

### B. Frontend — `backoffice`

```powershell
# 1. Di chuyển vào thư mục backoffice
cd backoffice

# 2. Cài đặt dependencies (nếu chưa có node_modules)
npm install

# 3. Chạy test + coverage
npm run test:coverage

# 4. Quay lại thư mục gốc
cd ..

# 5. Kiểm tra Quality Gate
.\scripts\check_quality_gate.ps1 -Service backoffice
```

**Kết quả mong đợi:**
```
Coverage for backoffice is 100%
Quality Gate PASSED: Coverage is > 70%
```

---

### C. Backend BFF — `storefront-bff`

> ⚠️ **Quan trọng:** Phải chạy test + tạo báo cáo Jacoco **TRƯỚC**, rồi mới chạy script Quality Gate **SAU**. Nếu chạy Quality Gate trước khi có báo cáo sẽ bị lỗi `Jacoco report not found`.

```powershell
# 1. Di chuyển vào thư mục storefront-bff
cd storefront-bff

# 2. Chạy test + tạo báo cáo JaCoCo (dùng Maven Wrapper có sẵn, KHÔNG CẦN cài Maven)
./mvnw test jacoco:report

# 3. (Tuỳ chọn) Mở báo cáo HTML để xem chi tiết
Invoke-Item target\site\jacoco\index.html

# 4. Quay lại thư mục gốc
cd ..

# 5. Kiểm tra Quality Gate
.\scripts\check_quality_gate.ps1 -Service storefront-bff
```

**Kết quả mong đợi:**
```
Tests run: 33, Failures: 0, Errors: 0, Skipped: 0
...
Coverage for storefront-bff is 100%
Quality Gate PASSED: Coverage is > 70%
```

> 💡 **Tip:** `storefront-bff` có **Maven Wrapper** (`mvnw`/`mvnw.cmd`) nên **không cần** cài đặt Apache Maven vào máy. Chỉ cần chạy `./mvnw` thay vì `mvn`.

---

### D. Backend BFF — `backoffice-bff`

```powershell
# 1. Di chuyển vào thư mục backoffice-bff
cd backoffice-bff

# 2. Chạy test + tạo báo cáo JaCoCo
./mvnw test jacoco:report

# 3. (Tuỳ chọn) Mở báo cáo HTML
Invoke-Item target\site\jacoco\index.html

# 4. Quay lại thư mục gốc
cd ..

# 5. Kiểm tra Quality Gate
.\scripts\check_quality_gate.ps1 -Service backoffice-bff
```

**Kết quả mong đợi:**
```
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
...
Coverage for backoffice-bff is 100%
Quality Gate PASSED: Coverage is > 70%
```

---

### E. Backend Java service khác (Ví dụ: `product`)

> ⚠️ **Lưu ý:** Các Java service ở root **không có Maven Wrapper** riêng, cần cài Apache Maven vào máy và cấu hình biến môi trường PATH. Nếu báo lỗi `The term 'mvn' is not recognized` → do chưa cài Maven.

```powershell
# 1. Chạy test cho module product (từ thư mục gốc)
mvn clean test -pl product -am

# 2. Kiểm tra Quality Gate
.\scripts\check_quality_gate.ps1 -Service product
```

---

## 6. Lỗi thường gặp và cách fix

| Lỗi | Nguyên nhân | Cách fix |
| --- | --- | --- |
| `Jacoco report not found at ...jacoco.csv` | Chưa chạy `test jacoco:report` trước khi check QG | Chạy `./mvnw test jacoco:report` trong thư mục service trước |
| `Skipping JaCoCo execution due to missing execution data file` | Service chưa có JaCoCo plugin trong `pom.xml` | Thêm `jacoco-maven-plugin` vào `<build><plugins>` |
| `The term 'mvn' is not recognized` | Chưa cài Apache Maven | Dùng `./mvnw` (Maven Wrapper) nếu có, hoặc cài Maven |
| `Cannot find path target\site\jacoco\index.html` | Dùng `start` thay vì `Invoke-Item` | Dùng `Invoke-Item target\site\jacoco\index.html` |
| `Unknown lifecycle phase .run.arguments=...` | PowerShell xử lý dấu `=` khác bash | Bọc argument trong nháy kép: `"-Dspring-boot.run.arguments=..."` |
| `Unknown option "setupFilesAfterSetup"` | Viết sai tên config key Jest | Đổi thành `setupFilesAfterEnv` |
| `Stmts < 100%` do constants file | Constants chưa được import hết | Thêm file test cho constants |

---

## 7. Tổng kết toàn bộ

### Thống kê tổng hợp

| Service | Loại | Công cụ test | Số tests | Coverage | Quality Gate |
| --- | --- | --- | --- | --- | --- |
| `storefront` | Frontend (Next.js) | Jest | 39 | **100%** | ✅ PASSED |
| `backoffice` | Frontend (Next.js) | Jest | 43 | **100%** | ✅ PASSED |
| `storefront-bff` | Backend (Java) | JUnit 5 + Mockito | 33 | **100%** | ✅ PASSED |
| `backoffice-bff` | Backend (Java) | JUnit 5 + Mockito | 10 | **100%** | ✅ PASSED |
| **Tổng cộng** | | | **125** | **100%** | ✅ **ALL PASSED** |

### Các file đã tạo/sửa

| File | Hành động | Mô tả |
| --- | --- | --- |
| `scripts/check_quality_gate.sh` | ✨ Tạo mới | Script bash cho CI |
| `scripts/check_quality_gate.ps1` | ✨ Tạo mới | Script PowerShell cho local |
| `.github/workflows/ci.yml` | ✨ Tạo mới | CI Pipeline GitHub Actions |
| `storefront/jest.config.js` | 📝 Sửa | Thêm `coverageReporters` |
| `backoffice/jest.config.js` | 📝 Sửa | Thêm `coverageReporters` |
| `backoffice-bff/pom.xml` | 📝 Sửa | Thêm `jacoco-maven-plugin` |
| `storefront-bff/src/test/java/...` | ✨ Tạo mới | 8 file test Java |
| `backoffice-bff/src/test/java/...` | ✨ Tạo mới | 2 file test Java |
| `storefront/utils/*.test.ts` | ✨ Tạo mới | 4 file test TypeScript |
| `backoffice/utils/*.test.ts` + ... | ✨ Tạo mới | 5 file test TypeScript |

### Khắc phục lỗi (Theo Review và Feedback)

| Issue | Cách giải quyết |
| --- | --- |
| Thiếu JaCoCo version | Đã thêm `<version>0.8.14</version>` (do project dùng Java 25) và cấu hình `<executions>` vào cả `backoffice-bff/pom.xml` và `storefront-bff/pom.xml`. |
| Lỗi Implicit `any` trong `renderHook` | Thêm type định dạng chính xác `({ value, delay }: { value: string | undefined; delay: number })` vào `useDebounce.test.ts`. |
| Thiếu test case `async` error | Bổ sung test cho lỗi `Invalid JSON` (khi `json()` parsing fail) trong `ResponseStatusHandlingService.test.ts`. |
| Cấu hình port Keycloak conflict | Đổi từ `80:80` thành `8080:80` trong `docker-compose.yml` và `setup-frontend-bff-local.md`. |
| Khắc phục lỗi bảo mật NPE | Thêm `null check` cho `OAuth2User principal` trong `AuthenticationController.java`, throw `IllegalStateException`. |
| Hard-code giá trị Test Options | Đổi `expect(toast.success).toHaveBeenCalledWith(..., {...})` thành `expect.objectContaining({...})` trong `ToastService.test.ts`. |
| Script dependencies dư thừa | Gỡ bỏ `ts-jest` và `@testing-library/user-event` khỏi hướng dẫn cài đặt `unit-test-storefront-guide.md` & `unit-test-backoffice-guide.md`. |
| Đồng bộ `package-lock.json` | Đã chạy lại `npm install` để đồng bộ lockfile ở cả hai thư mục `storefront` và `backoffice`. |
