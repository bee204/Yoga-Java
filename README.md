# VITA Yoga Center - Java Spring Boot

Hệ thống Quản lý và Đào tạo Yoga chuyên nghiệp xây dựng trên nền tảng **Java 17 LTS**, **Spring Boot 3.4.11**, **Spring Data JPA**, **Spring Security 6** và **Thymeleaf**.

> 📄 **Tài liệu hướng dẫn trực quan tương tác (Gồm Tab chuyển đổi & Ảnh chụp màn hình thực tế):** Mở file [`SYSTEM_DOCUMENTATION.html`](file:///c:/Users/vuduy/Documents/Project/Yoga-Java/SYSTEM_DOCUMENTATION.html) trên trình duyệt.

---

## 📸 Bộ Ảnh Chụp Màn Hình Giao Diện Thực Tế

Toàn bộ ảnh chụp thực tế từ hệ thống được lưu tại thư mục [`docs/screenshots/`](file:///c:/Users/vuduy/Documents/Project/Yoga-Java/docs/screenshots/):
* 🖥️ **Trang chủ học viên:** `docs/screenshots/home.png`
* 🧘 **Danh mục lớp học:** `docs/screenshots/classes.png`
* 👨‍🏫 **Đội ngũ giáo viên:** `docs/screenshots/teachers.png`
* 🔐 **Đăng nhập Học viên (Leaf theme):** `docs/screenshots/account_login.png`
* 🧑‍🏫 **Đăng nhập Giáo viên (Split layout):** `docs/screenshots/teacher_login.png`
* 🛡️ **Đăng nhập Quản trị viên:** `docs/screenshots/admin_login.png`
* 📊 **Quản trị Dashboard (Bộ lọc ngày & SVG Charts):** `docs/screenshots/admin_dashboard.png`
* 📋 **Quản lý & Duyệt đơn đăng ký:** `docs/screenshots/admin_registrations.png`
* 🏫 **Quản trị lớp học (Bộ lọc từ khóa, giáo viên, lịch học):** `docs/screenshots/admin_classes.png`
* 👥 **Quản lý hồ sơ học viên (Bộ lọc từ khóa, giới tính):** `docs/screenshots/admin_customers.png`
* 📑 **Hồ sơ vận hành lớp học:** `docs/screenshots/admin_class_detail.png`
* 👨‍🏫 **Cổng làm việc Giáo viên:** `docs/screenshots/teacher_dashboard.png`
* 👤 **Hồ sơ cá nhân & Đổi mật khẩu (UI Zen Forest):** `docs/screenshots/student_profile.png`
* 📚 **Lớp đã đăng ký của học viên:** `docs/screenshots/student_registered_classes.png`

---

## 🌟 Tính Năng Hệ Thống (Cập Nhật Mới Nhất)

### 1. Phân hệ Khách hàng / Học viên (User Site)
* **Trang chủ (`/`):** Thống kê trung tâm thời gian thực, thông điệp sức khỏe, danh mục giới thiệu khóa học. Khi người dùng **Đăng xuất (Logout)**, hệ thống tự động điều hướng an toàn về Trang chủ kèm thông báo xác nhận.
* **Danh mục lớp học (`/classes`):** Tìm kiếm trực tiếp (tên lớp, giáo viên, địa điểm), xem sĩ số, khung giờ, học phí, lịch tập.
* **Chi tiết lớp học (`/classes/{id}`):** Thông tin giảng viên hướng dẫn, số chỗ còn trống, trạng thái đăng ký.
* **Danh sách giáo viên (`/teachers` & `/teachers/{id}`):** Tra cứu hồ sơ, số năm kinh nghiệm, các lớp đang phụ trách.
* **Đăng ký lớp học (`/register`):** Tự động tính toán chiết khấu gói tập (3 tháng - 5%, 6 tháng - 10%, 12 tháng - 20%), chọn lớp và gửi đơn đăng ký.
* **Xác thực tài khoản (`/account/login`, `/account/register`):** Đăng nhập bằng Email/Tên đăng nhập, đăng ký học viên mới.
* **Hồ sơ cá nhân (`/account`):** Xem và cập nhật số điện thoại, địa chỉ, đổi mật khẩu tài khoản với giao diện Zen Forest trang nhã.
* **Khóa học của tôi (`/registered-classes`):** Theo dõi trạng thái đơn (`PENDING`, `CONFIRMED`, `CANCELLED`), hủy đơn, gửi đánh giá (1-5 sao kèm bình luận).
* **Nhóm tác giả (`/authors` hoặc `/team`):** Thông tin đội ngũ phát triển đồ án (Mã sinh viên, phân công nhiệm vụ).

### 2. Phân hệ Quản trị viên (Admin Control Center)
* **Đăng nhập quản trị (`/admin/login`):** Giao diện đăng nhập riêng biệt, bảo mật bằng Spring Security.
* **Dashboard tổng quan (`/admin/dashboard`):** 
  * **Bộ lọc ngày thông minh:** Lọc nhanh theo *Hôm nay*, *7 ngày qua*, *30 ngày qua*, *Tháng này* hoặc chọn khoảng ngày tùy chỉnh (`from_date` đến `to_date`).
  * **Tính toán thời gian thực:** Thống kê số đơn phát sinh, số đơn đã duyệt, số đơn chờ duyệt, tỷ lệ phần trăm và tổng doanh thu thực thu trong kỳ lọc.
  * **Biểu đồ Analytics:** Biểu đồ SVG tỷ lệ duyệt đơn, tăng trưởng học viên, phân bổ giáo viên và doanh số theo tháng.
* **Quản lý đơn đăng ký (`/admin/registrations`):** Lọc theo trạng thái (Tất cả, Chờ duyệt, Đã duyệt, Đã hủy). Nút **Phê duyệt** (`CONFIRMED`) và **Từ chối** (`CANCELLED`) một chạm.
* **Quản lý lớp học (`/admin/classes`):** Thêm mới, sửa, xóa lớp học. Tích hợp thanh tìm kiếm theo tên/địa điểm, lọc theo Giáo viên và lọc theo Lịch học.
* **Quản lý giáo viên (`/admin/teachers`):** Thêm, sửa, xóa giảng viên. Tích hợp tìm kiếm theo tên, số điện thoại, email và lọc theo Số năm kinh nghiệm.
* **Quản lý học viên (`/admin/customers`):** Thêm, sửa, xóa học viên. Tích hợp tìm kiếm đa tiêu chí và lọc theo Giới tính.
* **Điểm danh buổi học (`/admin/classes/{id}/attendance`):** Điểm danh Có mặt, Đi muộn, Vắng mặt theo từng lớp.
* **Quản lý đánh giá (`/admin/classes/{id}/reviews`):** Xem và kiểm duyệt phản hồi từ học viên.

### 3. Cổng Giáo viên Yoga (`/teacher/dashboard`)
* **Đăng nhập Giáo viên (`/teacher/login`):** Giao diện chuyên biệt cho HLV với hình ảnh giảng dạy trực quan.
* **Tổng quan lớp phụ trách (`/teacher/dashboard`):** Xem lịch dạy, khung giờ, phòng học của tất cả các lớp được phân công.
* **Chi tiết lớp Yoga (`/teacher/classes/{id}`):** Xem thông tin sĩ số và danh sách học viên chính thức trong lớp.
* **Điểm danh buổi học (`/teacher/classes/{id}/attendance`):** Ghi nhận trạng thái Có mặt (`PRESENT`), Đi muộn (`LATE`), Vắng mặt (`ABSENT`) của học viên trong buổi dạy.
* **Đánh giá học viên (`/teacher/classes/{id}/reviews`):** Xem trực tiếp toàn bộ nhận xét và số sao đánh giá từ học viên lớp mình.

---

## 🔑 Danh Sách Tài Khoản Mẫu (Đã Seed Sẵn Vào MySQL & H2)

Mật khẩu chung cho tất cả các tài khoản mẫu là: `123456`

| Vai trò | Tên đăng nhập | Email thay thế | Mật khẩu | Quyền hạn & Cổng truy cập |
|---|---|---|---|---|
| **Quản trị viên (Admin)** | `admin` | `admin@vitayoga.vn` | `123456` | Toàn quyền quản trị hệ thống tại `/admin/login` |
| **Giáo viên 1 (HLV Cẩm Tú)** | `teacher1` | `camtu@vitayoga.vn` | `123456` | Vào Cổng giáo viên `/teacher/dashboard` |
| **Giáo viên 2 (HLV Thu Hương)** | `teacher2` | `thuhuong@vitayoga.vn` | `123456` | Vào Cổng giáo viên `/teacher/dashboard` |
| **Giáo viên 3 (HLV Trọng Lực)** | `teacher3` | `trongluc@vitayoga.vn` | `123456` | Vào Cổng giáo viên `/teacher/dashboard` |
| **Học viên 1 (Nguyễn Văn An)** | `student1` hoặc `an.nguyen` | `an.nguyen@gmail.com` | `123456` | Đã có lớp học xác nhận (`CONFIRMED`), đăng nhập tại `/account/login` |
| **Học viên 2 (Trần Thị Mai)** | `student2` hoặc `mai.tran` | `mai.tran@gmail.com` | `123456` | Đã có đơn đăng ký chờ duyệt (`PENDING`) |

---

## 🧪 Bộ Kiểm Thử Tự Động Toàn Diện (Automated Test Suite)

Dự án tích hợp sẵn kịch bản kiểm thử tự động khép kín toàn bộ luồng nghiệp vụ trong file [`test_system_flows.js`](file:///c:/Users/vuduy/Documents/Project/Yoga-Java/test_system_flows.js).

Chạy lệnh kiểm thử:
```powershell
node test_system_flows.js
```

**Kết quả kiểm thử đạt 37/37 Test Cases (100% PASS):**
1. Xác thực tài khoản Admin & Học viên.
2. Bộ lọc ngày thời gian thực trên Admin Dashboard (Hôm nay, 7 ngày, 30 ngày, tùy chỉnh).
3. CRUD & Bộ lọc Lớp học Yoga (Tạo -> Đọc/Lọc -> Sửa -> Xóa).
4. CRUD & Bộ lọc Giáo viên Yoga (Tạo -> Đọc/Lọc -> Sửa -> Xóa).
5. CRUD & Bộ lọc Học viên (Tạo -> Đọc/Lọc -> Sửa -> Xóa).
6. Quy trình Học viên đặt lớp -> Đơn ở trạng thái `PENDING` -> Admin duyệt đơn thành `CONFIRMED` -> Học viên xem danh sách lớp đã duyệt.
7. Học viên cập nhật hồ sơ cá nhân -> Đổi mật khẩu -> Đăng nhập lại bằng mật khẩu mới thành công -> Khôi phục mật khẩu mẫu.

---

## 🚀 Hướng Dẫn Chạy Trên Máy Local

### Cách 1: Chạy nhanh với Database ảo H2 (Không cần cài MySQL)

Chạy lệnh sau tại thư mục `Yoga-Java`:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=test"
```

Khi terminal hiện `Tomcat started on port 8080 (http)`:
* **Trang chủ học viên:** [http://localhost:8080](http://localhost:8080)
* **Trang quản trị:** [http://localhost:8080/admin/login](http://localhost:8080/admin/login) (Đăng nhập: `admin` / `123456`)
* **Kiểm tra API:** [http://localhost:8080/api/ping](http://localhost:8080/api/ping)

---

### Cách 2: Chạy với Cơ Sở Dữ Liệu MySQL Thật (XAMPP)

1. Bật MySQL trên **XAMPP Control Panel** (cổng `3306`).
2. Mở MySQL Workbench hoặc phpMyAdmin tạo database:
   ```sql
   CREATE DATABASE IF NOT EXISTS yoga_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Khởi chạy ứng dụng:
   ```powershell
   mvn spring-boot:run
   ```
   *(Cấu hình `spring.jpa.hibernate.ddl-auto=update` sẽ tự động tạo bảng và nạp dữ liệu mẫu chuẩn UTF-8 vào MySQL).*

---

## 📁 Cấu Trúc Mã Nguồn

```text
Yoga-Java/
├── src/
│   ├── main/
│   │   ├── java/com/yoga/vita/
│   │   │   ├── config/DataInitializer.java      # Tự động nạp tài khoản admin và dữ liệu mẫu
│   │   │   ├── controller/
│   │   │   │   ├── WebController.java          # Điều hướng toàn bộ giao diện User Site
│   │   │   │   ├── AdminWebController.java     # Điều hướng và xử lý CRUD Admin, Dashboard & Filters
│   │   │   │   ├── TeacherWebController.java   # Cổng giáo viên: Chi tiết lớp, Điểm danh, Đánh giá
│   │   │   │   ├── AuthController.java         # REST API xác thực
│   │   │   │   └── PublicCatalogController.java# REST API danh mục công khai
│   │   │   ├── entity/                         # Các JPA Entities (User, Class, Teacher, Registration...)
│   │   │   ├── repository/                     # Spring Data JPA Repositories
│   │   │   ├── security/SecurityConfig.java    # Cấu hình phân quyền & Spring Security, Logout redirect
│   │   │   └── VitaApplication.java            # Main Application
│   │   └── resources/
│   │       ├── static/                         # CSS, JS, Images từ bản gốc
│   │       ├── templates/                      # Toàn bộ giao diện HTML Thymeleaf
│   │       │   ├── components/                 # Header, Footer, Account Sidebar
│   │       │   ├── layouts/                    # Layout Admin Nav
│   │       │   ├── pages/                      # Dashboard, Classes, Teachers, Register, Profile...
│   │       │   ├── admin/                      # Dashboard, Classes, Teachers, Customers, Attendance...
│   │       │   └── teacher/                    # Teacher Portal workspace
│   │       ├── application.properties          # Cấu hình môi trường MySQL
│   │       └── application-test.properties     # Cấu hình môi trường H2 In-Memory
│   └── test/
│       └── java/com/yoga/vita/                 # Unit & Integration Tests
├── docs/screenshots/                           # 15 ảnh chụp màn hình giao diện thực tế
├── SYSTEM_DOCUMENTATION.html                   # Tài liệu hướng dẫn tương tác trực quan đa tab
├── test_system_flows.js                        # Kịch bản kiểm thử tự động toàn diện (37 test cases)
├── pom.xml                                     # Maven dependencies
└── README.md                                   # Tài liệu dự án
```
