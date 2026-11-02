package com.yoga.vita.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "registrations")
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "class_id")
    private Long classId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private YogaClass yogaClass;

    @Column(name = "package_months")
    private Integer packageMonths;

    @Column(precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(length = 50)
    private String status;

    @Column(length = 255)
    private String note;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @OneToMany(mappedBy = "registrationId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public YogaClass getYogaClass() { return yogaClass; }
    public void setYogaClass(YogaClass yogaClass) { this.yogaClass = yogaClass; }

    public Integer getPackageMonths() { return packageMonths != null ? packageMonths : 1; }
    public void setPackageMonths(Integer packageMonths) { this.packageMonths = packageMonths; }

    public BigDecimal getDiscount() { return discount != null ? discount : BigDecimal.ZERO; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getFinalPrice() { return finalPrice != null ? finalPrice : BigDecimal.ZERO; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public String getStatus() { return status != null ? status : "PENDING"; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
