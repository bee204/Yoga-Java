package com.yoga.vita.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="teachers")
public class Teacher {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String name; private String email; private String phone; private String avatar; private String specialization; private String experience; private String bio;
 @Column(name="created_at",updatable=false) private LocalDateTime createdAt; @Column(name="updated_at") private LocalDateTime updatedAt;
 @PrePersist void p(){createdAt=LocalDateTime.now();updatedAt=createdAt;} @PreUpdate void u(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAvatar(){return avatar;} public void setAvatar(String v){avatar=v;} public String getSpecialization(){return specialization;} public void setSpecialization(String v){specialization=v;} public String getExperience(){return experience;} public void setExperience(String v){experience=v;} public String getBio(){return bio;} public void setBio(String v){bio=v;}
}
