package com.yoga.vita.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="class_reviews")
public class ClassReview {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="customer_id") private Long customerId; @Column(name="class_id") private Long classId; private Integer rating; private String comment;
 @Column(name="created_at",updatable=false) private LocalDateTime createdAt; @Column(name="updated_at") private LocalDateTime updatedAt;
 @PrePersist void p(){createdAt=LocalDateTime.now();updatedAt=createdAt;} @PreUpdate void u(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;} public Long getClassId(){return classId;} public void setClassId(Long v){classId=v;} public Integer getRating(){return rating;} public void setRating(Integer v){rating=v;} public String getComment(){return comment;} public void setComment(String v){comment=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
