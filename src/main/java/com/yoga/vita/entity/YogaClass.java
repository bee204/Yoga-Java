package com.yoga.vita.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "classes")
public class YogaClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id")
    private Long teacherId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "lich_hoc", length = 50)
    private String lichHoc;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 100)
    private String location;

    @Column(length = 255)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "yogaClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations;

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
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLichHoc() { return lichHoc; }
    public void setLichHoc(String lichHoc) { this.lichHoc = lichHoc; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getQuantity() { return quantity != null ? quantity : 0; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price != null ? price : BigDecimal.ZERO; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @JsonIgnore
    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }

    @JsonIgnore
    public int getConfirmedCount() {
        try {
            if (registrations == null) return 0;
            return (int) registrations.stream()
                    .filter(r -> "CONFIRMED".equalsIgnoreCase(r.getStatus()))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    @JsonIgnore
    public int getAvailableSlots() {
        return Math.max(0, getQuantity() - getConfirmedCount());
    }

    @JsonIgnore
    public boolean isFull() {
        return getAvailableSlots() <= 0;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
