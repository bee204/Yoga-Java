package com.yoga.vita.repository;

import com.yoga.vita.entity.ClassReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClassReviewRepository extends JpaRepository<ClassReview, Long> {
    List<ClassReview> findByClassId(Long classId);
    List<ClassReview> findByCustomerId(Long customerId);
    Optional<ClassReview> findByCustomerIdAndClassId(Long customerId, Long classId);
    boolean existsByCustomerIdAndClassId(Long customerId, Long classId);
    void deleteByClassId(Long classId);
}
