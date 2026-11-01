package com.yoga.vita.controller;
import com.yoga.vita.entity.*; import com.yoga.vita.repository.*;
import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.util.*;
@RestController @RequestMapping("/api/account")
public class AccountController {
 private final UserRepository users; private final CustomerRepository customers; private final PasswordEncoder encoder;
 public AccountController(UserRepository users,CustomerRepository customers,PasswordEncoder encoder){this.users=users;this.customers=customers;this.encoder=encoder;}
 private User current(Authentication a){return users.findByEmail(a.getName()).or(() -> users.findByUserName(a.getName())).orElseThrow();}
 @GetMapping("/profile") public Map<String,Object> profile(Authentication a){User u=current(a); Customer c=u.getCustomerId()==null?null:customers.findById(u.getCustomerId()).orElse(null); Map<String,Object> d=new LinkedHashMap<>();d.put("id",u.getId());d.put("name",u.getName());d.put("email",u.getEmail());d.put("phone",c==null?null:c.getPhone());d.put("birthday",c==null?null:c.getBirthday());d.put("gender",c==null?null:c.getGender());d.put("address",c==null?null:c.getAddress());return Map.of("data",d);}
 record Profile(String name,String phone,LocalDate birthday,String gender,String address){}
 @PutMapping("/profile") public ResponseEntity<?> update(Authentication a,@RequestBody Profile body){User u=current(a);Customer c=u.getCustomerId()==null?null:customers.findById(u.getCustomerId()).orElse(null);if(c==null)return ResponseEntity.status(422).body(Map.of("message","Tài khoản chưa được liên kết với hồ sơ thành viên."));c.setName(body.name());c.setPhone(body.phone());c.setBirthday(body.birthday());c.setGender(body.gender());c.setAddress(body.address());customers.save(c);u.setName(body.name());users.save(u);return ResponseEntity.ok(Map.of("message","Cập nhật hồ sơ thành công.","data",profile(a).get("data")));}
 record Password(String password){}
 @PutMapping("/password") public ResponseEntity<?> password(Authentication a,@RequestBody Password body,HttpServletRequest request){User u=current(a);if(body.password()==null||body.password().length()<6)return ResponseEntity.badRequest().body(Map.of("message","Mật khẩu phải có ít nhất 6 ký tự."));u.setPassword(encoder.encode(body.password()));users.save(u);return ResponseEntity.ok(Map.of("message","Đổi mật khẩu thành công. Các API token cũ đã được thu hồi."));}
}
