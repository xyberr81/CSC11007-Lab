# Hướng dẫn: Dựng môi trường local cho Frontend/BFF (YAS Project)

> Tài liệu này được viết dựa trên thực tế triển khai trên `PowerShell`, bao gồm các lỗi đã gặp và cách fix.

---

## Yêu cầu cài đặt trước

| Công cụ        | Phiên bản               | Ghi chú                             |
| -------------- | ----------------------- | ----------------------------------- |
| Docker Desktop | Latest                  | Cần đủ RAM ≥ 8GB                    |
| Java           | **21** (khuyến nghị)    | Dùng Adoptium: https://adoptium.net |
| Node.js        | 18+                     | Dùng nvm để quản lý version         |
| Maven          | Wrapper `./mvnw` có sẵn | Không cần cài thêm                  |

### Kiểm tra trước khi bắt đầu

```powershell
docker --version
java -version       # phải là 21.x.x
node --version      # phải là v18+
```

---

## Bước 1: Clone repository

```powershell
git clone https://github.com/<your-org>/yas.git
cd yas
```

---

## Bước 2: Cấu hình file hosts

> ⚠️ **YAS dùng domain nội bộ, bạn phải thêm vào file hosts của máy.** Đây là bước hay bị quên nhất và gây lỗi khi chạy.

1. Mở **Notepad** với quyền **Run as Administrator**
2. Mở file: `C:\Windows\System32\drivers\etc\hosts`
3. Thêm dòng sau vào **cuối file**:

   ```
    127.0.0.1 identity
    127.0.0.1 api.yas.local
    127.0.0.1 pgadmin.yas.local
    127.0.0.1 storefront
    127.0.0.1 backoffice
    127.0.0.1 loki
    127.0.0.1 tempo
    127.0.0.1 grafana
    127.0.0.1 elasticsearch
    127.0.0.1 kafka

   ```

4. Lưu file (Ctrl+S)

Kiểm tra thử `identity`:

```powershell
Get-Content C:\Windows\System32\drivers\etc\hosts | Select-String "identity"
# Phải thấy: 127.0.0.1    identity
```

---

## Bước 3: Thêm port mapping cho Keycloak trong docker-compose.yml

> ⚠️ Mặc định, service `identity` trong `docker-compose.yml` **không expose port ra ngoài**, khiến máy host không kết nối được.

Mở file `docker-compose.yml` ở thư mục gốc, tìm service `identity` và thêm `ports`:

```yaml
identity:
  image: quay.io/keycloak/keycloak:26.0.2
  command: "start-dev --import-realm"
  environment:
    KC_HTTP_PORT: 80
    # ... các biến khác giữ nguyên
  ports:
    - "8080:80" # ← THÊM DÒNG NÀY
  volumes:
    - ./identity/realm-export.json:/opt/keycloak/data/import/realm-export.json
    - ./identity/themes/yas/theme:/opt/keycloak/themes
  networks:
    - yas-network
```

---

## Bước 4: Khởi động Docker infrastructure theo từng nhóm

> ⚠️ **KHÔNG chạy `docker compose up -d` toàn bộ một lúc.** Pull ~25 image song song sẽ làm Docker Engine crash với lỗi `fatal error: concurrent map writes`.

### Nhóm 1 — Core infrastructure (quan trọng nhất)

```powershell
cd D:\...\yas   # thư mục gốc chứa docker-compose.yml

docker compose -f docker-compose.yml up -d postgres identity
```

Đợi **2–3 phút** rồi kiểm tra:

```powershell
docker compose -f docker-compose.yml ps
```

Cần thấy cả `yas-postgres-1` và `yas-identity-1` ở trạng thái `Up`.

### Xác nhận Keycloak đã sẵn sàng

```powershell
docker compose -f docker-compose.yml logs identity --tail=20
```

Tìm dòng:

```
Keycloak 26.0.2 on JVM ... started ... Listening on: http://0.0.0.0:80
```

Khi thấy dòng này → Keycloak OK. Test thêm:

```powershell
curl http://identity/realms/Yas/.well-known/openid-configuration
# Phải trả về JSON dài
```

### Nhóm 2 — Các service phụ trợ

```powershell
docker compose -f docker-compose.yml up -d zookeeper kafka redis
```

### Nhóm 3 — Chạy toàn bộ Backend

```powershell
docker compose -f docker-compose.yml up -d media product customer cart tax location inventory promotion rating payment order sampledata
```

### Nhóm 4 — Frontend & Nginx

```powershell
docker compose -f docker-compose.yml up -d nginx storefront-nextjs backoffice-nextjs
```

### Nhóm 5 — Các service còn lại (tuỳ chọn, nếu cần test đầy đủ)

```powershell
docker compose -f docker-compose.yml -f docker-compose.search.yml up -d kafka-connect kafka-ui pgadmin swagger-ui elasticsearch
```

> ℹ️ Service `nginx` có thể ở trạng thái `Restarting` nếu các service backend chưa chạy

---

## Bước 5: Chạy Storefront-BFF (Spring Boot)

```powershell
cd storefront-bff

# Build trước (skip test để nhanh)
./mvnw clean install -DskipTests

# Chạy service
./mvnw spring-boot:run
```

### Dấu hiệu thành công

Thấy log:

```
Started StorefrontBffApplication in X.XXX seconds
```

Service chạy ở port mặc định **8888** (kiểm tra `application.properties`).

### Lỗi thường gặp & cách fix

| Lỗi                                                     | Nguyên nhân                                          | Cách fix          |
| ------------------------------------------------------- | ---------------------------------------------------- | ----------------- |
| `Unable to resolve Issuer "http://identity/realms/Yas"` | Chưa thêm hosts file                                 | Làm lại Bước 2    |
| `Connection refused: getsockopt`                        | Keycloak chưa expose port                            | Làm lại Bước 3    |
| `Unexpected end of file from server`                    | Keycloak chưa khởi động xong                         | Đợi thêm 1–2 phút |
| `BUILD FAILURE - exit code 1`                           | Xem log phần `Caused by:` để tìm nguyên nhân thật sự |

---

## Bước 6: Chạy Backoffice-BFF (Spring Boot)

Mở terminal mới (giữ Storefront-BFF đang chạy). Khi chạy local bằng ./mvnw thì cả hai service tranh nhau cùng một port, nên cần assign port khác cho backoffice-bff để tránh conflict:

```powershell
cd backoffice-bff
./mvnw clean install -DskipTests
./mvnw spring-boot:run "-Dspring-boot.run.arguments=--server.port=8088"
```

---

## Bước 7: Chạy Storefront (Next.js)

Mở terminal mới:

```powershell
cd storefront

# Cài dependencies
npm install

# Tạo file cấu hình môi trường
# Kiểm tra và chỉnh sửa .env. nếu cần

# Chạy dev server
npm run dev
```

Truy cập: **http://localhost:3000**

---

## Bước 8: Chạy Backoffice (Next.js)

Mở terminal mới:

```powershell
cd backoffice
npm install
npm run dev
```

Truy cập: **http://localhost:3001** (hoặc port khác tuỳ config)

---

## Bước 9: Build thử (kiểm tra pipeline)

### Next.js production build

```powershell
# Storefront
cd storefront
npm run build

# Backoffice
cd backoffice
npm run build
```

Build thành công → không có lỗi đỏ, thấy dòng `✓ Compiled successfully`.

### Spring Boot production build

```powershell
# Storefront-BFF
cd storefront-bff
./mvnw clean package -DskipTests

# Backoffice-BFF
cd backoffice-bff
./mvnw clean package -DskipTests
```

File JAR được tạo tại `target/*.jar` → build thành công.

---

## Bước 10: Chạy Unit Test & xem Coverage

```powershell
# Storefront-BFF
cd storefront-bff
./mvnw test
# Xem báo cáo coverage tại: target/site/jacoco/index.html

# Storefront (Next.js - Jest)
cd storefront
npm test -- --coverage
```

---

## Checklist tổng hợp

- [ ] Cài đủ Docker, Java 21, Node.js 18+
- [ ] Thêm `127.0.0.1 identity` vào hosts file (Run as Admin)
- [ ] Thêm `ports: - "80:80"` cho service `identity` trong `docker-compose.yml`
- [ ] Start `postgres` và `identity` trước, đợi Keycloak sẵn sàng
- [ ] Verify Keycloak: `curl http://identity/realms/Yas/.well-known/openid-configuration`
- [ ] Start `redis`, `zookeeper`, `kafka`
- [ ] Chạy `storefront-bff` → thấy "Started" trong log
- [ ] Chạy `backoffice-bff` → thấy "Started" trong log
- [ ] Chạy `storefront` (Next.js) → truy cập http://localhost:3000
- [ ] Chạy `backoffice` (Next.js) → truy cập http://localhost:3001
- [ ] Build thử `npm run build` và `./mvnw clean package` → không lỗi

---

## Lưu ý quan trọng

- Luôn chạy lệnh `docker compose` từ **thư mục gốc** chứa `docker-compose.yml`, không phải từ thư mục con của từng service.
- Mỗi service (BFF, Storefront, Backoffice) cần chạy trong **terminal riêng biệt**.
- Warning `version is obsolete` trong docker compose là bình thường, không ảnh hưởng.
- Java version 25 tuy chạy được nhưng nên dùng Java 21 đúng theo yêu cầu của project.
