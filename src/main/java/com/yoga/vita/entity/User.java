package com.yoga.vita.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="user_name", nullable=false) private String userName;
    private String name;
    private String email;
    private String password;
    private String role;
    @Column(name="customer_id") private Long customerId;
    @Column(name="teacher_id") private Long teacherId;
    @Column(name="remember_token") private String rememberToken;
    @Column(name="avatar") private String avatar;
    @Column(name="profile_phone") private String profilePhone;
    @Column(name="profile_address") private String profileAddress;
    @Column(name="created_at", updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at") private LocalDateTime updatedAt;
    @PrePersist void prePersist(){createdAt=LocalDateTime.now(); updatedAt=createdAt;} @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getUserName(){return userName;} public void setUserName(String userName){this.userName=userName;} public String getName(){return name;} public void setName(String name){this.name=name;} public String getEmail(){return email;} public void setEmail(String email){this.email=email;} public String getPassword(){return password;} public void setPassword(String password){this.password=password;} public String getRole(){return role;} public void setRole(String role){this.role=role;} public Long getCustomerId(){return customerId;} public void setCustomerId(Long customerId){this.customerId=customerId;} public Long getTeacherId(){return teacherId;} public void setTeacherId(Long teacherId){this.teacherId=teacherId;}
}
