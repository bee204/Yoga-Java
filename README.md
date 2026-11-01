# Yoga Java

## 1. Clone project từ GitHub

Mở Terminal hoặc PowerShell, chọn thư mục muốn lưu project rồi chạy:

```powershell
git clone https://github.com/TEN_TAI_KHOAN/yoga-java.git
cd yoga-java
```

Nếu đã biết đường dẫn repository, có thể thay bằng liên kết clone trong nút **Code** trên GitHub.

Nếu không dùng Git, nhấn **Code > Download ZIP** trên GitHub, giải nén rồi mở thư mục chứa file `pom.xml`.

## 2. Tạo và cấu hình database

Mở MySQL Workbench, phpMyAdmin hoặc terminal MySQL rồi chạy:

```sql
CREATE DATABASE IF NOT EXISTS yoga_management;
```

Mở file:

```text
src/main/resources/application.properties
```

Kiểm tra thông tin kết nối, thay tài khoản và mật khẩu MySQL nếu máy của bạn khác mặc định:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/yoga_management
spring.datasource.username=root
spring.datasource.password=
```

Nếu MySQL có mật khẩu, điền mật khẩu vào dòng `spring.datasource.password`.

> Database cần có sẵn các bảng của project. Project không tự tạo hoặc thay đổi bảng (`ddl-auto=validate`).

## 3. Chạy project bằng VS Code

1. Mở thư mục vừa clone bằng **File > Open Folder**.
2. Đợi VS Code nhận diện project Maven.
3. Nếu được yêu cầu, cài extension **Extension Pack for Java**.
4. Mở file:

```text
src/main/java/com/yoga/vita/VitaApplication.java
```

5. Nhấn nút **Run** cạnh class `VitaApplication`.

## 4. Chạy project bằng Terminal

Mở Terminal tại thư mục chứa file `pom.xml`, sau đó chạy:

```powershell
mvn spring-boot:run
```

Chờ terminal hiển thị project đã khởi động tại:

<http://localhost:8080>

Mở trình duyệt và kiểm tra API tại:

<http://localhost:8080/api/ping>

Nếu thấy kết quả JSON thì chạy thành công.

## 5. Chạy bằng IntelliJ IDEA hoặc NetBeans

### IntelliJ IDEA

1. Chọn **Open** và mở thư mục chứa file `pom.xml`.
2. Đợi Maven tải thư viện.
3. Mở `VitaApplication.java`.
4. Nhấn nút **Run** cạnh tên class.

### NetBeans

1. Mở thư mục project bằng **File > Open Project**.
2. Chuột phải project.
3. Chọn **Run** hoặc **Run Maven > spring-boot:run**.

## Lỗi thường gặp

- `Unknown database yoga_management`: kiểm tra đã chạy câu lệnh tạo database ở mục 2.
- Không kết nối được MySQL: kiểm tra MySQL đang chạy, username/password và cổng `3306`.
- Cổng `8080` đã được sử dụng: đổi `server.port=${PORT:8080}` trong `application.properties` thành `server.port=8081`, sau đó mở `http://localhost:8081/api/ping`.
- Nếu không chạy được Java hoặc Maven: kiểm tra máy đã cài **JDK 17** và Maven trước.
