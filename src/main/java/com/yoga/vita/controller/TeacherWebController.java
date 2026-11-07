package com.yoga.vita.controller;

import com.yoga.vita.entity.*;
import com.yoga.vita.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/teacher")
public class TeacherWebController {

    private final TeacherRepository teachers;
    private final ClassRepository classes;
    private final UserRepository users;
    private final RegistrationRepository registrations;
    private final AttendanceRepository attendances;
    private final ClassReviewRepository reviews;

    public TeacherWebController(TeacherRepository teachers,
                                ClassRepository classes,
                                UserRepository users,
                                RegistrationRepository registrations,
                                AttendanceRepository attendances,
                                ClassReviewRepository reviews) {
        this.teachers = teachers;
        this.classes = classes;
        this.users = users;
        this.registrations = registrations;
        this.attendances = attendances;
        this.reviews = reviews;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String name = auth.getName();
        return users.findByEmail(name).or(() -> users.findByUserName(name)).orElse(null);
    }

    private Teacher getCurrentTeacher(User u) {
        if (u == null) return null;
        if (u.getTeacherId() != null) {
            return teachers.findById(u.getTeacherId()).orElse(null);
        }
        List<Teacher> all = teachers.findAll();
        return all.isEmpty() ? null : all.get(0);
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/teacher/login";

        Teacher t = getCurrentTeacher(u);
        List<YogaClass> myClasses = t != null ? classes.findByTeacherId(t.getId()) : Collections.emptyList();

        model.addAttribute("teacher", t);
        model.addAttribute("classes", myClasses);
        model.addAttribute("currentUser", u);
        return "teacher/dashboard";
    }

    @GetMapping("/classes/{id}")
    public String classDetail(@PathVariable Long id, Model model) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/teacher/login";

        YogaClass c = classes.findById(id).orElse(null);
        if (c == null) return "redirect:/teacher/dashboard";

        List<Registration> regs = registrations.findByClassId(id);
        model.addAttribute("class", c);
        model.addAttribute("registrations", regs);
        model.addAttribute("currentUser", u);
        return "admin/class_detail";
    }

    @GetMapping("/classes/{id}/attendance")
    public String classAttendance(@PathVariable Long id, Model model) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/teacher/login";

        YogaClass c = classes.findById(id).orElse(null);
        if (c == null) return "redirect:/teacher/dashboard";

        List<Registration> confirmed = registrations.findByClassIdAndStatus(id, "CONFIRMED");
        model.addAttribute("class", c);
        model.addAttribute("registrations", confirmed);
        model.addAttribute("currentUser", u);
        return "admin/attendance";
    }

    @PostMapping("/classes/{id}/attendance")
    public String saveAttendance(@PathVariable Long id,
                                 @RequestParam("registration_id") Long regId,
                                 @RequestParam("status") String status,
                                 @RequestParam(value = "note", required = false) String note,
                                 RedirectAttributes flash) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/teacher/login";

        Attendance a = new Attendance();
        a.setRegistrationId(regId);
        a.setDate(LocalDate.now());
        a.setStatus(status);
        a.setNote(note);
        attendances.save(a);

        flash.addFlashAttribute("success", "Điểm danh học viên thành công!");
        return "redirect:/teacher/classes/" + id + "/attendance";
    }

    @GetMapping("/classes/{id}/reviews")
    public String classReviews(@PathVariable Long id, Model model) {
        User u = getAuthenticatedUser();
        if (u == null) return "redirect:/teacher/login";

        YogaClass c = classes.findById(id).orElse(null);
        if (c == null) return "redirect:/teacher/dashboard";

        model.addAttribute("class", c);
        model.addAttribute("reviews", reviews.findByClassId(id));
        model.addAttribute("currentUser", u);
        return "admin/class_reviews";
    }
}
