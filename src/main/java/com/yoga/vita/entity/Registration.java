package com.yoga.vita.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="registrations")
public class Registration {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="customer_id") private Long customerId; @Column(name="class_id") private Long classId;
 @Column(name="package_months") private Integer packageMonths; private BigDecimal discount; @Column(name="final_price") private BigDecimal finalPrice; private String status; private String note; @Column(name="idempotency_key") private String idempotencyKey;
 @Column(name="created_at",updatable=false) private LocalDateTime createdAt; @Column(name="updated_at") private LocalDateTime updatedAt; @Column(name="deleted_at") private LocalDateTime deletedAt;
 @PrePersist void p(){createdAt=LocalDateTime.now();updatedAt=createdAt;} @PreUpdate void u(){updatedAt=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;} public Long getClassId(){return classId;} public void setClassId(Long v){classId=v;} public Integer getPackageMonths(){return packageMonths;} public void setPackageMonths(Integer v){packageMonths=v;} public BigDecimal getDiscount(){return discount;} public void setDiscount(BigDecimal v){discount=v;} public BigDecimal getFinalPrice(){return finalPrice;} public void setFinalPrice(BigDecimal v){finalPrice=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getNote(){return note;} public void setNote(String v){note=v;} public String getIdempotencyKey(){return idempotencyKey;} public void setIdempotencyKey(String v){idempotencyKey=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
