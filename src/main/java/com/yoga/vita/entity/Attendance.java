package com.yoga.vita.entity;
import jakarta.persistence.*; import java.time.LocalDate; import java.time.LocalDateTime;
@Entity @Table(name="attendances")
public class Attendance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="registration_id") private Long registrationId; @Column(name="attendance_date") private LocalDate date; private String status; private String note;
 @Column(name="created_at",updatable=false) private LocalDateTime createdAt; @Column(name="updated_at") private LocalDateTime updatedAt;
 @PrePersist void p(){createdAt=LocalDateTime.now();updatedAt=createdAt;} @PreUpdate void u(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public Long getRegistrationId(){return registrationId;} public void setRegistrationId(Long v){registrationId=v;} public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getNote(){return note;} public void setNote(String v){note=v;}
}
