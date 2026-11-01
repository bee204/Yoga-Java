package com.yoga.vita.controller;
import com.yoga.vita.entity.*; import com.yoga.vita.repository.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api")
public class AdminApiController {
 private final TeacherRepository teachers; private final ClassRepository classes; private final CustomerRepository customers; private final RegistrationRepository registrations;
 public AdminApiController(TeacherRepository t,ClassRepository c,CustomerRepository cu,RegistrationRepository r){teachers=t;classes=c;customers=cu;registrations=r;}
 @GetMapping("/teachers") public List<Teacher> getTeachers(){return teachers.findAll();}
 @PostMapping("/teachers") public ResponseEntity<Teacher> addTeacher(@RequestBody Teacher t){return ResponseEntity.status(201).body(teachers.save(t));}
 @GetMapping("/teachers/{id}") public Teacher teacher(@PathVariable Long id){return teachers.findById(id).orElseThrow();}
 @PutMapping("/teachers/{id}") public Teacher updateTeacher(@PathVariable Long id,@RequestBody Teacher t){t.setId(id);return teachers.save(t);} @DeleteMapping("/teachers/{id}") public ResponseEntity<Void> deleteTeacher(@PathVariable Long id){teachers.deleteById(id);return ResponseEntity.noContent().build();}
 @GetMapping("/classes") public List<YogaClass> getClasses(){return classes.findAll();}
 @PostMapping("/classes") public ResponseEntity<YogaClass> addClass(@RequestBody YogaClass c){return ResponseEntity.status(201).body(classes.save(c));}
 @GetMapping("/classes/{id}") public YogaClass clazz(@PathVariable Long id){return classes.findById(id).orElseThrow();}
 @PutMapping("/classes/{id}") public YogaClass updateClass(@PathVariable Long id,@RequestBody YogaClass c){c.setId(id);return classes.save(c);} @DeleteMapping("/classes/{id}") public ResponseEntity<Void> deleteClass(@PathVariable Long id){classes.deleteById(id);return ResponseEntity.noContent().build();}
 @GetMapping("/customers") public List<Customer> getCustomers(){return customers.findAll();}
 @PostMapping("/customers") public ResponseEntity<Customer> addCustomer(@RequestBody Customer c){return ResponseEntity.status(201).body(customers.save(c));}
 @GetMapping("/customers/{id}") public Customer customer(@PathVariable Long id){return customers.findById(id).orElseThrow();}
 @PutMapping("/customers/{id}") public Customer updateCustomer(@PathVariable Long id,@RequestBody Customer c){c.setId(id);return customers.save(c);} @DeleteMapping("/customers/{id}") public ResponseEntity<Void> deleteCustomer(@PathVariable Long id){customers.deleteById(id);return ResponseEntity.noContent().build();}
 @GetMapping("/registrations") public List<Registration> getRegistrations(){return registrations.findAll();}
 @PostMapping("/registrations") public ResponseEntity<?> addRegistration(@RequestHeader(value="Idempotency-Key",required=false) String key,@RequestBody Registration r){if(key!=null){var old=registrations.findByIdempotencyKey(key);if(old.isPresent())return ResponseEntity.ok(old.get());r.setIdempotencyKey(key);}return ResponseEntity.status(201).body(registrations.save(r));}
 @GetMapping("/registrations/{id}") public Registration registration(@PathVariable Long id){return registrations.findById(id).orElseThrow();}
 @PutMapping("/registrations/{id}") public Registration updateRegistration(@PathVariable Long id,@RequestBody Registration r){r.setId(id);return registrations.save(r);} @DeleteMapping("/registrations/{id}") public ResponseEntity<Void> deleteRegistration(@PathVariable Long id){registrations.deleteById(id);return ResponseEntity.noContent().build();}
 @PostMapping("/registrations/{id}/confirm") public Registration confirm(@PathVariable Long id){var r=registration(id);r.setStatus("CONFIRMED");return registrations.save(r);} @PostMapping("/registrations/{id}/cancel") public Registration cancel(@PathVariable Long id){var r=registration(id);r.setStatus("CANCELLED");return registrations.save(r);}
}
