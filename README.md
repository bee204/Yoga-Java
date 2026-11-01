# VITA Yoga — Spring Boot backend

## Trạng thái chuyển đổi

Backend Java Spring Boot đã được tạo tại thư mục này để thay thế các API Laravel theo cùng cơ sở dữ liệu MySQL hiện có. Frontend Blade/CSS/JS gốc được giữ nguyên ở thư mục dự án PHP. File Java API hiện có đầy đủ catalog public, đăng nhập, tài khoản, tài nguyên CRUD, điểm danh/đánh giá và thống kê cơ bản.

> Phạm vi còn công khai: các trang Blade trả HTML cần được render bởi một frontend server độc lập hoặc một lớp Thymeleaf tương đương. Không thể cam kết 100% tương thích khi chưa chuyển từng action Blade và chạy đối chiếu end-to-end với database thật.

## Công nghệ
- Java 17
- Spring Boot 3.4
- Spring MVC, Spring Security, Spring Data JPA
- MySQL 8 (không đổi schema)
- H2 chỉ dùng cho test

## Cấu hình
Cấu hình trong `src/main/resources/application.properties`, có thể ghi đè bằng biến môi trường:

```powershell
$env:DB_URL="jdbc:mysql://127.0.0.1:3306/yoga_management"
$env:DB_USERNAME="root"
$env:DB_PASSWORD=""
cd backend-java
mvn spring-boot:run
```

Không chạy migration/schema làm thay đổi database. Backend dùng `ddl-auto=validate`.

## Build và test
```powershell
cd backend-java
mvn clean test
mvn clean package
java -jar target/vita-1.0.0.jar
```

## API tương thích
- `GET /api/ping`
- `POST /api/login`, `GET /api/me`, `POST /api/logout`
- Public: `GET /api/public/teachers`, `/api/public/teachers/{id}`, `/api/public/classes`, `/api/public/classes/{id}`
- Account: `GET /api/account/profile`, `PUT /api/account/profile`, `PUT /api/account/password`
- Resource admin: `/api/teachers`, `/api/classes`, `/api/customers`, `/api/registrations`
- Confirm/cancel: `POST /api/registrations/{id}/confirm`, `POST /api/registrations/{id}/cancel`
- Attendance/review: `/api/attendance`, `/api/attendance/summary`, `/api/class-reviews`

## Mapping PHP → Java
| PHP | Java |
|---|---|
| `routes/api.php` | `controller/*Controller.java`, `security/SecurityConfig.java` |
| `app/Http/Controllers/AuthController.php` | `AuthController.java` |
| `PublicCatalogController.php` | `PublicCatalogController.java` |
| `AccountController.php` | `AccountController.java` |
| `TeacherController.php`, `YogaClassController.php`, `CustomerController.php`, `RegistrationController.php` | `AdminApiController.java` |
| `AttendanceController.php`, `ClassReviewController.php` | `OperationsController.java` |
| `app/Models/*.php` | `entity/*.java` |
| Eloquent repositories | Spring Data repositories trong `repository/*.java` |
