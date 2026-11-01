package com.yoga.vita.controller;
import com.yoga.vita.entity.*; import com.yoga.vita.repository.*;
import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController
public class OperationsController {
 private final AttendanceRepository attendances; private final ClassReviewRepository reviews; private final RegistrationRepository registrations; private final UserRepository users; private final CustomerRepository customers;
 public OperationsController(AttendanceRepository a,ClassReviewRepository r,RegistrationRepository rg,UserRepository u,CustomerRepository c){attendances=a;reviews=r;registrations=rg;users=u;customers=c;}
 @GetMapping("/api/attendance") public List<Attendance> listAttendance(){return attendances.findAll();}
 @PostMapping("/api/attendance") public ResponseEntity<Attendance> createAttendance(@RequestBody Attendance a){return ResponseEntity.status(201).body(attendances.save(a));}
 @PutMapping("/api/attendance/{id}") public Attendance updateAttendance(@PathVariable Long id,@RequestBody Attendance a){a.setId(id);return attendances.save(a);}
 @GetMapping("/api/attendance/summary") public Map<String,Object> attendanceSummary(){return Map.of("total",attendances.count());}
 @GetMapping("/api/class-reviews/ranking") public List<ClassReview> ranking(){return reviews.findAll();}
 @GetMapping("/api/class-reviews") public List<ClassReview> reviewList(){return reviews.findAll();}
 @PostMapping("/api/class-reviews") public ResponseEntity<ClassReview> createReview(@RequestBody ClassReview r){return ResponseEntity.status(201).body(reviews.save(r));}
 @PutMapping("/api/class-reviews/{id}") public ClassReview updateReview(@PathVariable Long id,@RequestBody ClassReview r){r.setId(id);return reviews.save(r);}
 @DeleteMapping("/api/class-reviews/{id}") public ResponseEntity<Void> deleteReview(@PathVariable Long id){reviews.deleteById(id);return ResponseEntity.noContent().build();}
}
