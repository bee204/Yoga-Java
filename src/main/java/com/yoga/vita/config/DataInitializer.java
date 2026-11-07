package com.yoga.vita.config;

import com.yoga.vita.entity.*;
import com.yoga.vita.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository users;
    private final TeacherRepository teachers;
    private final ClassRepository classes;
    private final CustomerRepository customers;
    private final RegistrationRepository registrations;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository users, TeacherRepository teachers, ClassRepository classes,
                           CustomerRepository customers, RegistrationRepository registrations,
                           PasswordEncoder encoder) {
        this.users = users;
        this.teachers = teachers;
        this.classes = classes;
        this.customers = customers;
        this.registrations = registrations;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        boolean forceReSeed = Arrays.asList(args).contains("--reseed");
        if (forceReSeed) {
            log.info("==> [SEEDER] Tham số --reseed được kích hoạt. Đang xóa và khởi tạo lại toàn bộ dữ liệu...");
            registrations.deleteAll();
            classes.deleteAll();
            customers.deleteAll();
            teachers.deleteAll();
            users.deleteAll();
        }

        seedData();
    }

    public synchronized void seedData() {
        // 1. Seed Admin Account
        if (users.findByUserName("admin").isEmpty()) {
            User admin = new User();
            admin.setUserName("admin");
            admin.setName("Administrator");
            admin.setEmail("admin@vitayoga.vn");
            admin.setPassword(encoder.encode("123456"));
            admin.setRole("admin");
            users.save(admin);
            log.info("==> [SEEDER] Đã tạo tài khoản quản trị: admin / 123456");
        }

        // 2. Seed 5 Teachers
        if (teachers.count() == 0) {
            Teacher t1 = new Teacher();
            t1.setName("Nguyễn Thị Cẩm Tú");
            t1.setPhone("0901234001");
            t1.setEmail("camtu@vitayoga.vn");
            t1.setBirthday(LocalDate.of(1996, 5, 12));
            t1.setExpYear(3);
            t1.setDescription("Giảng viên Hatha Yoga và Yin Yoga chuyên sâu, chứng chỉ RYT 200 Yoga Alliance.");
            t1.setAvatar("tu.jpg");
            teachers.save(t1);

            Teacher t2 = new Teacher();
            t2.setName("Nguyễn Thị Thu Hương");
            t2.setPhone("0901234002");
            t2.setEmail("thuhuong@vitayoga.vn");
            t2.setBirthday(LocalDate.of(1994, 8, 20));
            t2.setExpYear(5);
            t2.setDescription("Chuyên gia Vinyasa Flow và Yoga phục hồi thể trạng, giàu năng lượng truyền cảm hứng.");
            t2.setAvatar("huong.jpg");
            teachers.save(t2);

            Teacher t3 = new Teacher();
            t3.setName("Hoàng Trọng Lực");
            t3.setPhone("0901234003");
            t3.setEmail("trongluc@vitayoga.vn");
            t3.setBirthday(LocalDate.of(1993, 3, 15));
            t3.setExpYear(4);
            t3.setDescription("Huấn luyện viên Power Yoga & Core, tập trung kiểm soát cơ bụng và chỉnh sửa tư thế chuẩn.");
            t3.setAvatar("luc.jpg");
            teachers.save(t3);

            Teacher t4 = new Teacher();
            t4.setName("Nguyễn Trung Hiếu");
            t4.setPhone("0901234004");
            t4.setEmail("trunghieu@vitayoga.vn");
            t4.setBirthday(LocalDate.of(1995, 11, 28));
            t4.setExpYear(3);
            t4.setDescription("Giảng viên Yoga thiền định và thở Pranayama, giúp giảm căng thẳng và cải thiện giấc ngủ.");
            t4.setAvatar("hieu.jpg");
            teachers.save(t4);

            Teacher t5 = new Teacher();
            t5.setName("Vũ Huy Năng");
            t5.setPhone("0901234005");
            t5.setEmail("huynang@vitayoga.vn");
            t5.setBirthday(LocalDate.of(1992, 9, 10));
            t5.setExpYear(6);
            t5.setDescription("Master Yoga với hơn 6 năm kinh nghiệm giảng dạy Ashtanga và đào tạo HLV Yoga tại Hà Nội.");
            t5.setAvatar("hung.jpg");
            teachers.save(t5);

            // Tạo tài khoản User đăng nhập cho 5 giáo viên (teacher1 -> teacher5)
            List<Teacher> allT = teachers.findAll();
            int tIdx = 1;
            for (Teacher t : allT) {
                if (users.findByEmail(t.getEmail()).isEmpty() && users.findByUserName("teacher" + tIdx).isEmpty()) {
                    User tu = new User();
                    tu.setUserName("teacher" + tIdx);
                    tu.setName(t.getName());
                    tu.setEmail(t.getEmail());
                    tu.setPassword(encoder.encode("123456"));
                    tu.setRole("teacher");
                    tu.setTeacherId(t.getId());
                    users.save(tu);
                }
                tIdx++;
            }

            log.info("==> [SEEDER] Đã nạp thành công 5 giảng viên Yoga kèm tài khoản đăng nhập.");
        }

        // 3. Seed 8 Classes
        if (classes.count() == 0) {
            List<Teacher> tList = teachers.findAll();
            Teacher t1 = tList.get(0);
            Teacher t2 = tList.size() > 1 ? tList.get(1) : t1;
            Teacher t3 = tList.size() > 2 ? tList.get(2) : t1;
            Teacher t4 = tList.size() > 3 ? tList.get(3) : t1;
            Teacher t5 = tList.size() > 4 ? tList.get(4) : t1;

            createClass("Yoga Cân Bằng & Thư Giãn", t1.getId(), "Thứ 2 - 4 - 6", LocalTime.of(6, 0), LocalTime.of(7, 15), 20, "500000", "Phòng Sen Vàng - Tầng 2", "Khóa học phù hợp cho người mới bắt đầu và dân văn phòng cần thư giãn cơ bắp.");
            createClass("Vinyasa Flow Năng Lượng", t2.getId(), "Thứ 3 - 5 - 7", LocalTime.of(18, 0), LocalTime.of(19, 15), 18, "650000", "Phòng Trúc Xanh - Tầng 3", "Chuỗi chuyển động liên tục kết hợp hơi thở sâu giúp tăng cường đốt mỡ và săn chắc vóc dáng.");
            createClass("Power Yoga & Cột Sống Khỏe", t3.getId(), "Thứ 7 - Chủ Nhật", LocalTime.of(8, 0), LocalTime.of(9, 30), 15, "600000", "Phòng Năng Lượng - Tầng 2", "Tăng cường sức mạnh cơ cốt lõi (Core), giải tỏa áp lực cột sống và tăng sức bền thể chất.");
            createClass("Yin Yoga Trị Liệu Giấc Ngủ", t4.getId(), "Thứ 2 - 4 - 6", LocalTime.of(19, 30), LocalTime.of(20, 45), 20, "550000", "Phòng Thiền Tịnh - Tầng 4", "Giữ tư thế tĩnh từ 3-5 phút giúp kéo giãn sâu các mô liên kết và xoa dịu hệ thần kinh.");
            createClass("Ashtanga Yoga Cường Độ Cao", t5.getId(), "Thứ 3 - 5 - 7", LocalTime.of(6, 0), LocalTime.of(7, 30), 12, "800000", "Phòng Shala - Tầng 3", "Trường phái yoga truyền thống kỷ luật cao, rèn luyện ý chí và thanh lọc toàn diện cơ thể.");
            createClass("Hatha Yoga Cho Người Mới", t1.getId(), "Thứ 2 - 4 - 6", LocalTime.of(17, 30), LocalTime.of(18, 45), 25, "500000", "Phòng Sen Trắng - Tầng 2", "Học cách thở đúng kỹ thuật và thực hiện các asana căn bản an toàn nhất.");
            createClass("Yoga Dẻo Dai & Thon Gọn", t2.getId(), "Thứ 3 - 5 - 7", LocalTime.of(19, 30), LocalTime.of(20, 45), 16, "700000", "Phòng Trúc Xanh - Tầng 3", "Tập trung thon gọn đùi, eo, nâng mông và tăng độ linh hoạt khớp háng.");
            createClass("Yoga Trị Liệu Cổ Vai Gáy", t4.getId(), "Thứ 7 - Chủ Nhật", LocalTime.of(15, 0), LocalTime.of(16, 30), 20, "600000", "Phòng Thiền Tịnh - Tầng 4", "Bài tập chuyên biệt hỗ trợ giảm đau mỏi đốt sống cổ và thắt lưng cho người ngồi nhiều.");

            log.info("==> [SEEDER] Đã nạp thành công 8 lớp học đa dạng.");
        }

        // 4. Seed Demo Customers & Registrations
        if (customers.count() == 0) {
            String[][] customerData = {
                {"Nguyễn Văn An", "an.nguyen@gmail.com", "0988776655", "Hà Nội", "male"},
                {"Trần Thị Mai", "mai.tran@gmail.com", "0987654321", "Cầu Giấy, Hà Nội", "female"},
                {"Lê Văn Hùng", "hung.le@gmail.com", "0912345678", "Đống Đa, Hà Nội", "male"},
                {"Phạm Thị Lan", "lan.pham@gmail.com", "0934567890", "Thanh Xuân, Hà Nội", "female"},
                {"Vũ Hoàng Nam", "nam.vu@gmail.com", "0978901234", "Ba Đình, Hà Nội", "male"}
            };

            List<Customer> savedCustomers = new ArrayList<>();
            for (String[] cd : customerData) {
                Customer c = new Customer();
                c.setName(cd[0]);
                c.setEmail(cd[1]);
                c.setPhone(cd[2]);
                c.setAddress(cd[3]);
                c.setGender(cd[4]);
                c.setBirthday(LocalDate.of(1995, 3, 20));
                savedCustomers.add(customers.save(c));

                // Tạo tài khoản User tương ứng
                if (users.findByEmail(c.getEmail()).isEmpty()) {
                    User u = new User();
                    u.setUserName(c.getEmail().split("@")[0]);
                    u.setName(c.getName());
                    u.setEmail(c.getEmail());
                    u.setPassword(encoder.encode("123456"));
                    u.setRole("customer");
                    u.setCustomerId(c.getId());
                    users.save(u);
                }
            }

            // Seed Đơn Đăng Ký mẫu
            List<YogaClass> allCls = classes.findAll();
            if (!allCls.isEmpty() && !savedCustomers.isEmpty()) {
                // Đơn 1: CONFIRMED
                createRegistration(savedCustomers.get(0).getId(), allCls.get(0).getId(), 3, "5.00", "1425000.00", "CONFIRMED", "Đã hoàn thành chuyển khoản");
                // Đơn 2: PENDING
                createRegistration(savedCustomers.get(1).getId(), allCls.get(1).getId(), 6, "10.00", "3510000.00", "PENDING", "Chờ admin kiểm tra thanh toán");
                // Đơn 3: CONFIRMED
                if (allCls.size() > 2) {
                    createRegistration(savedCustomers.get(2).getId(), allCls.get(2).getId(), 1, "0.00", "600000.00", "CONFIRMED", "Đóng trực tiếp tại quầy lễ tân");
                }
                // Đơn 4: PENDING
                if (allCls.size() > 3) {
                    createRegistration(savedCustomers.get(3).getId(), allCls.get(3).getId(), 12, "20.00", "5280000.00", "PENDING", "Đăng ký gói VIP năm");
                }
            }

            log.info("==> [SEEDER] Đã nạp thành công dữ liệu học viên mẫu và đơn đăng ký.");
        }
    }

    private void createClass(String name, Long teacherId, String schedule, LocalTime start, LocalTime end, int qty, String price, String loc, String desc) {
        YogaClass c = new YogaClass();
        c.setName(name);
        c.setTeacherId(teacherId);
        c.setLichHoc(schedule);
        c.setStartTime(start);
        c.setEndTime(end);
        c.setStartDate(LocalDate.now());
        c.setEndDate(LocalDate.now().plusMonths(3));
        c.setQuantity(qty);
        c.setPrice(new BigDecimal(price));
        c.setLocation(loc);
        c.setDescription(desc);
        classes.save(c);
    }

    private void createRegistration(Long custId, Long classId, int months, String discount, String finalPrice, String status, String note) {
        Registration r = new Registration();
        r.setCustomerId(custId);
        r.setClassId(classId);
        r.setPackageMonths(months);
        r.setDiscount(new BigDecimal(discount));
        r.setFinalPrice(new BigDecimal(finalPrice));
        r.setStatus(status);
        r.setNote(note);
        registrations.save(r);
    }
}
