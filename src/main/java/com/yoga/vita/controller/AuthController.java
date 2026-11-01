package com.yoga.vita.controller;
import com.yoga.vita.entity.User; import com.yoga.vita.repository.UserRepository; import jakarta.servlet.http.HttpServletRequest; import jakarta.servlet.http.HttpSession;
import org.springframework.http.*; import org.springframework.security.authentication.*; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.context.HttpSessionSecurityContextRepository; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api")
public class AuthController {
 private final AuthenticationManager auth; private final UserRepository users; private final PasswordEncoder encoder;
 public AuthController(AuthenticationManager auth,UserRepository users,PasswordEncoder encoder){this.auth=auth;this.users=users;this.encoder=encoder;}
 record Login(String email,String password,String username){}
 @GetMapping("/ping") Map<String,String> ping(){return Map.of("message","API is working");}
 @PostMapping("/login") ResponseEntity<?> login(@RequestBody Login body,HttpServletRequest request){
  String login=body.email()!=null?body.email():body.username(); if(login==null)return ResponseEntity.status(422).body(Map.of("message","The email field is required."));
  try{var authentication=auth.authenticate(new UsernamePasswordAuthenticationToken(login,body.password())); SecurityContextHolder.getContext().setAuthentication(authentication); HttpSession session=request.getSession(true); session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,SecurityContextHolder.getContext()); User user=users.findByEmail(login).or(() -> users.findByUserName(login)).orElseThrow(); Map<String,Object> result=new LinkedHashMap<>(); result.put("user",user); return ResponseEntity.ok(result);}catch(BadCredentialsException e){return ResponseEntity.status(401).body(Map.of("message","Thông tin đăng nhập không đúng!"));}
 }
 @GetMapping("/me") Object me(){var a=SecurityContextHolder.getContext().getAuthentication(); if(a==null||!a.isAuthenticated()) return Map.of(); return a.getPrincipal();}
 @PostMapping("/logout") ResponseEntity<?> logout(HttpServletRequest request){request.getSession(false); if(request.getSession(false)!=null)request.getSession(false).invalidate(); SecurityContextHolder.clearContext(); return ResponseEntity.ok(Map.of("message","Đã đăng xuất thành công."));}
}
