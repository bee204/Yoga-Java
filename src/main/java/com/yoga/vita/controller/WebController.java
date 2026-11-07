package com.yoga.vita.controller;

import com.yoga.vita.entity.*;
import com.yoga.vita.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class WebController {

    private final ClassRepository classes;
    private final TeacherRepository teachers;
    private final CustomerRepository customers;
    private final RegistrationRepository registrations;
    private final ClassReviewRepository reviews;
    private final AttendanceRepository attendances;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public WebController(ClassRepository classes, TeacherRepository teachers, CustomerRepository customers,
                         RegistrationRepository registrations, ClassReviewRepository reviews,
                         AttendanceRepository attendances, UserRepository users, PasswordEncoder encoder,
                         AuthenticationManager authManager) {
        this.classes = classes;
        this.teachers = teachers;
        this.customers = customers;
        this.registrations = registrations;
        this.reviews = reviews;
        this.attendances = attendances;
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
    }

    @ModelAttribute("currentUser")
    public User populateCurrentUser() {
        return getAuthenticatedUser();
    }

    @ModelAttribute("currentCustomer")
    public Customer populateCurrentCustomer() {
        return getAuthenticatedCustomer();
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : "";
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String name = auth.getName();
        return users.findByEmail(name).or(() -> users.findByUserName(name)).orElse(null);
    }

    private Customer getAuthenticatedCustomer() {
        User u = getAuthenticatedUser();
        if (u == null || u.getCustomerId() == null) return null;
        return customers.findById(u.getCustomerId()).orElse(null);
    }

    // 1. Dashboard / Home page
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("classesCount", classes.count());
        model.addAttribute("teachersCount", teachers.count());
        model.addAttribute("membersCount", customers.count());
        model.addAttribute("registrationsCount", registrations.count());
        model.addAttribute("currentUser", getAuthenticatedUser());
        return "pages/dashboard";
    }

    // 2. Classes catalog
    @GetMapping("/classes")
    public String classesList(Model model) {
        List<YogaClass> classList = classes.findAll();
        Customer customer = getAuthenticatedCustomer();
        Map<Long, Map<String, Object>> statusMap = new HashMap<>();

        if (customer != null) {
            List<Registration> myRegs = registrations.findByCustomerIdAndStatusIn(
                    customer.getId(), List.of("PENDING", "CONFIRMED"));
            for (Registration r : myRegs) {
                Map<String, Object> item = new HashMap<>();
                item.put("status", r.getStatus());
                statusMap.put(r.getClassId(), item);
            }
        }

        model.addAttribute("classes", classList);
        model.addAttribute("registrationStatuses", statusMap);
        model.addAttribute("currentUser", getAuthenticatedUser());
        return "pages/classes";
    }

    // 3. Class Detail
    @GetMapping("/classes/{id}")
    public String classDetail(@PathVariable Long id, Model model) {
        YogaClass c = classes.findById(id).orElse(null);
        if (c == null) return "redirect:/classes";

        List<Registration> confirmed = registrations.findByClassIdAndStatus(id, "CONFIRMED");
        List<Customer> students = new ArrayList<>();
        for (Registration r : confirmed) {
            if (r.getCustomer() != null) students.add(r.getCustomer());
        }

        Customer customer = getAuthenticatedCustomer();
        String myStatus = null;
        if (customer != null) {
            var reg = registrations.findByCustomerIdAndClassIdAndStatusIn(
                    customer.getId(), id, List.of("PENDING", "CONFIRMED"));
            if (reg.isPresent()) myStatus = reg.get().getStatus();
        }

        model.addAttribute("class", c);
        model.addAttribute("registeredStudents", students);
        model.addAttribute("availableSlots", c.getAvailableSlots());
        model.addAttribute("registrationStatus", myStatus);
        model.addAttribute("currentUser", getAuthenticatedUser());
        return "pages/class_detail";
    }

    // 4. Teachers list
    @GetMapping("/teachers")
    public String teachersList(Model model) {
        model.addAttribute("teachers", teachers.findAll());
        model.addAttribute("currentUser", getAuthenticatedUser());
        return "pages/teachers";
    }

    // 5. Teacher Detail
    @GetMapping("/teachers/{id}")
    public String teacherDetail(@PathVariable Long id, Model model) {
        Teacher t = teachers.findById(id).orElse(null);
        if (t == null) return "redirect:/teachers";

        List<YogaClass> teacherClasses = classes.findByTeacherId(id);
        model.addAttribute("teacher", t);
        model.addAttribute("teacherClasses", teacherClasses);
        model.addAttribute("currentUser", getAuthenticatedUser());
        return "pages/teacher_detail";
    }

    // 6. Authors / Team
    @GetMapping({"/authors", "/team"})
    public String authors(Model model) {
        model.addAttribute("currentUser", getAuthenticatedUser());
        return "pages/authors";
    }

    // 7. Register class page (GET)
    @GetMapping("/register")
    public String showRegister(@RequestParam(value = "class_id", required = false) Long classId,
                               Model model, RedirectAttributes flash) {
        User user = getAuthenticatedUser();
        if (user == null) {
            flash.addFlashAttribute("info", "Vui lòng đăng nhập hoặc tạo tài khoản để đăng ký lớp học.");
            return "redirect:/account/login";
        }

        List<YogaClass> all = classes.findAll();
        model.addAttribute("classes", all);
        model.addAttribute("selectedClassId", classId);
        model.addAttribute("user", user);
        model.addAttribute("customer", getAuthenticatedCustomer());
        model.addAttribute("currentUser", user);
        return "pages/register";
    }

    // 8. Register class submit (POST)
    @PostMapping("/register")
    public String registerSubmit(@RequestParam("class_id") Long classId,
                                 @RequestParam(value = "package_months", defaultValue = "1") int months,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "note", required = false) String note,
                                 RedirectAttributes flash) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/account/login";

        YogaClass c = classes.findById(classId).orElse(null);
        if (c == null) {
            flash.addFlashAttribute("error", "Lớp học không tồn tại.");
            return "redirect:/classes";
        }

        // Validate months
        if (months != 1 && months != 3 && months != 6 && months != 12) {
            months = 1;
        }

        // Check if class is full
        long confirmedCount = registrations.countByClassIdAndStatus(classId, "CONFIRMED");
        if (c.getQuantity() != null && confirmedCount >= c.getQuantity()) {
            flash.addFlashAttribute("error", "Lớp học đã đủ sĩ số (" + c.getQuantity() + " học viên). Vui lòng chọn lớp học khác!");
            return "redirect:/classes";
        }

        Customer cust = getAuthenticatedCustomer();
        if (cust == null) {
            cust = new Customer();
            cust.setName(name != null && !name.isBlank() ? name : user.getName());
            cust.setEmail(user.getEmail());
            cust.setPhone(phone != null && !phone.isBlank() ? phone : "0900000000");
            cust = customers.save(cust);
            user.setCustomerId(cust.getId());
            users.save(user);
        }

        // Check if already registered
        var existing = registrations.findByCustomerIdAndClassIdAndStatusIn(
                cust.getId(), classId, List.of("PENDING", "CONFIRMED"));
        if (existing.isPresent()) {
            flash.addFlashAttribute("error", "Bạn đã đăng ký lớp học này rồi.");
            return "redirect:/classes";
        }

        // Calculate discount
        BigDecimal discountPct = BigDecimal.ZERO;
        if (months == 3) discountPct = new BigDecimal("5.00");
        else if (months == 6) discountPct = new BigDecimal("10.00");
        else if (months == 12) discountPct = new BigDecimal("20.00");

        BigDecimal base = c.getPrice().multiply(new BigDecimal(months));
        BigDecimal finalPrice = base.multiply(BigDecimal.ONE.subtract(discountPct.divide(new BigDecimal("100"))));

        Registration reg = new Registration();
        reg.setCustomerId(cust.getId());
        reg.setClassId(classId);
        reg.setPackageMonths(months);
        reg.setDiscount(discountPct);
        reg.setFinalPrice(finalPrice);
        reg.setStatus("PENDING");
        reg.setNote(note);
        registrations.save(reg);

        flash.addFlashAttribute("success", "Đăng ký lớp học thành công! Đơn đăng ký đang chờ quản trị viên duyệt.");
        return "redirect:/registered-classes";
    }

    // 9. Login page (GET)
    @GetMapping("/account/login")
    public String showLoginAccount(@RequestParam(value = "portal", required = false) String portal,
                                   Model model) {
        if (getAuthenticatedUser() != null) return "redirect:/";
        model.addAttribute("portal", portal);
        return "pages/login_account";
    }

    @GetMapping("/teacher/login")
    public String showTeacherLogin(Model model) {
        if (getAuthenticatedUser() != null) return "redirect:/teacher/dashboard";
        return "pages/teacher_login";
    }

    // 10. Login submit (POST for both /account/login and /teacher/login)
    @PostMapping({"/account/login", "/teacher/login"})
    public String loginSubmit(@RequestParam(value = "email", required = false) String email,
                              @RequestParam(value = "username", required = false) String username,
                              @RequestParam("password") String password,
                              @RequestParam(value = "portal", required = false) String portal,
                              HttpServletRequest request,
                              RedirectAttributes flash) {
        String loginId = (email != null && !email.isBlank()) ? email.trim() : (username != null ? username.trim() : "");
        try {
            var token = new UsernamePasswordAuthenticationToken(loginId, password);
            Authentication auth = authManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            User u = users.findByEmail(loginId).or(() -> users.findByUserName(loginId)).orElse(null);
            if (u != null) {
                String role = u.getRole() == null ? "" : u.getRole().trim().toLowerCase();
                if ("teacher".equals(role)) {
                    flash.addFlashAttribute("success", "Đăng nhập thành công! Chào mừng Giáo viên " + (u.getName() != null ? u.getName() : u.getUserName()));
                    return "redirect:/teacher/dashboard";
                }
                if ("admin".equals(role)) {
                    flash.addFlashAttribute("success", "Đăng nhập thành công! Chào mừng Quản trị viên.");
                    return "redirect:/admin/dashboard";
                }
                flash.addFlashAttribute("success", "Đăng nhập thành công! Chào mừng " + (u.getName() != null ? u.getName() : "bạn quay trở lại."));
                return "redirect:/account";
            }

            flash.addFlashAttribute("success", "Đăng nhập thành công!");
            return "redirect:/";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Email/Tên đăng nhập hoặc mật khẩu không chính xác!");
            if (request.getRequestURI().contains("/teacher/login") || "teacher".equals(portal)) {
                return "redirect:/teacher/login";
            }
            return "redirect:/account/login";
        }
    }

    // 11. Register account page (GET)
    @GetMapping("/account/register")
    public String showRegisterAccount(Model model) {
        if (getAuthenticatedUser() != null) return "redirect:/";
        return "pages/register_account";
    }

    // 12. Register account submit (POST)
    @PostMapping("/account/register")
    public String registerAccountSubmit(@RequestParam("name") String name,
                                        @RequestParam("email") String email,
                                        @RequestParam("password") String password,
                                        @RequestParam("password_confirmation") String confirmation,
                                        RedirectAttributes flash) {
        if (!password.equals(confirmation)) {
            flash.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/account/register";
        }
        if (users.existsByEmail(email)) {
            flash.addFlashAttribute("error", "Email này đã được sử dụng!");
            return "redirect:/account/register";
        }

        // Create Customer
        Customer c = customers.findByEmail(email).orElse(null);
        if (c == null) {
            c = new Customer();
            c.setName(name);
            c.setEmail(email);
            c.setPhone("09" + (int)(10000000 + Math.random() * 90000000));
            c.setBirthday(LocalDate.of(1995, 1, 1));
            c.setGender("other");
            c = customers.save(c);
        }

        // Create User
        User u = new User();
        u.setUserName(email.split("@")[0] + "-" + System.currentTimeMillis() % 10000);
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setRole("customer");
        u.setCustomerId(c.getId());
        users.save(u);

        flash.addFlashAttribute("success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
        return "redirect:/account/login";
    }

    // Logout shortcut (GET)
    @GetMapping({"/logout", "/account/logout"})
    public String logoutGet(jakarta.servlet.http.HttpServletRequest request) {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return "redirect:/?logout";
    }

    // 13. Account profile (GET)
    @GetMapping("/account")
    public String accountProfile(Model model) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/account/login";

        Customer c = getAuthenticatedCustomer();
        model.addAttribute("user", u);
        model.addAttribute("customer", c);
        model.addAttribute("currentUser", u);
        return "pages/account_profile";
    }

    // 14. Update profile (POST)
    @PostMapping("/account/profile")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "gender", required = false) String gender,
                                @RequestParam(value = "address", required = false) String address,
                                RedirectAttributes flash) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/account/login";

        u.setName(name);
        users.save(u);

        Customer c = getAuthenticatedCustomer();
        if (c != null) {
            c.setName(name);
            if (phone != null) c.setPhone(phone);
            if (gender != null) c.setGender(gender);
            if (address != null) c.setAddress(address);
            customers.save(c);
        }

        flash.addFlashAttribute("success", "Cập nhật thông tin hồ sơ thành công!");
        return "redirect:/account";
    }

    // 15. Update password (POST)
    @PostMapping("/account/password")
    public String updatePassword(@RequestParam("password") String password,
                                 @RequestParam(value = "password_confirmation", required = false) String confirmation,
                                 @RequestParam(value = "confirm_password", required = false) String confirmPassword,
                                 RedirectAttributes flash) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/account/login";

        String actualConfirm = confirmation != null && !confirmation.isBlank() ? confirmation : confirmPassword;

        if (password.length() < 6) {
            flash.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "redirect:/account";
        }
        if (!password.equals(actualConfirm)) {
            flash.addFlashAttribute("error", "Xác nhận mật khẩu không khớp!");
            return "redirect:/account";
        }

        u.setPassword(encoder.encode(password));
        users.save(u);

        flash.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/account";
    }

    // 16. Registered classes (GET)
    @GetMapping("/registered-classes")
    public String registeredClasses(Model model) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/account/login";

        Customer c = getAuthenticatedCustomer();
        List<Registration> list = c != null
                ? registrations.findByCustomerIdAndStatusIn(c.getId(), List.of("PENDING", "CONFIRMED", "CANCELLED"))
                : Collections.emptyList();

        model.addAttribute("registrations", list);
        model.addAttribute("currentUser", u);
        model.addAttribute("user", u);
        return "pages/registered_classes";
    }

    // 17. Cancel registration (POST) - with ownership verification
    @PostMapping("/registered-classes/{id}/cancel")
    public String cancelRegistration(@PathVariable Long id, RedirectAttributes flash) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/account/login";

        Customer c = getAuthenticatedCustomer();
        Registration reg = registrations.findById(id).orElse(null);
        if (reg != null && c != null && reg.getCustomerId().equals(c.getId())) {
            reg.setStatus("CANCELLED");
            registrations.save(reg);
            flash.addFlashAttribute("success", "Đã hủy đơn đăng ký lớp học thành công.");
        } else {
            flash.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
        }
        return "redirect:/registered-classes";
    }

    // 18. Registered class detail & reviews
    @GetMapping("/registered-classes/{id}")
    public String registeredClassDetail(@PathVariable Long id, Model model, RedirectAttributes flash) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/account/login";

        Customer c = getAuthenticatedCustomer();
        YogaClass cls = classes.findById(id).orElse(null);
        if (cls == null || c == null) return "redirect:/registered-classes";

        boolean isEnrolled = registrations.findByCustomerIdAndStatusIn(c.getId(), List.of("CONFIRMED", "PENDING"))
                .stream().anyMatch(r -> r.getClassId().equals(id));
        if (!isEnrolled) {
            flash.addFlashAttribute("error", "Bạn chỉ có thể xem chi tiết các lớp học bạn đã đăng ký.");
            return "redirect:/registered-classes";
        }

        ClassReview review = reviews.findByCustomerIdAndClassId(c.getId(), id).orElse(null);

        model.addAttribute("class", cls);
        model.addAttribute("review", review);
        model.addAttribute("canSubmitReview", review == null);
        model.addAttribute("canEditReview", review != null);
        model.addAttribute("currentUser", u);
        return "pages/registered_class_detail";
    }

    // 19. Submit class review (POST)
    @PostMapping("/registered-classes/{id}/review")
    public String submitReview(@PathVariable Long id,
                               @RequestParam("rating") int rating,
                               @RequestParam(value = "comment", required = false) String comment,
                               RedirectAttributes flash) {
        User u = getAuthenticatedUser();
        Customer c = getAuthenticatedCustomer();
        if (u == null || c == null) return "redirect:/account/login";

        // Bounded rating 1..5
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;

        ClassReview r = reviews.findByCustomerIdAndClassId(c.getId(), id).orElse(new ClassReview());
        r.setCustomerId(c.getId());
        r.setClassId(id);
        r.setRating(rating);
        r.setComment(comment);
        reviews.save(r);

        flash.addFlashAttribute("success", "Cảm ơn bạn đã gửi đánh giá cho lớp học!");
        return "redirect:/registered-classes/" + id;
    }
}
