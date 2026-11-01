package com.yoga.vita.entity;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="customers")
public class Customer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String name; private String email; private String phone; private LocalDate birthday; private String gender; private String address;
 @Column(name="created_at",updatable=false) private LocalDateTime createdAt; @Column(name="updated_at") private LocalDateTime updatedAt; @Column(name="deleted_at") private LocalDateTime deletedAt;
 @PrePersist void p(){createdAt=LocalDateTime.now();updatedAt=createdAt;} @PreUpdate void u(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public LocalDate getBirthday(){return birthday;} public void setBirthday(LocalDate v){birthday=v;} public String getGender(){return gender;} public void setGender(String v){gender=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;}
}
