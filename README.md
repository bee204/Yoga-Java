# VITA Yoga — Spring Boot backend

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

## Build và test
```powershell
cd backend-java
mvn clean test
mvn clean package
java -jar target/vita-1.0.0.jar
```

