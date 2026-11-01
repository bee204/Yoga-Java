package com.yoga.vita.repository; import com.yoga.vita.entity.ClassReview; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ClassReviewRepository extends JpaRepository<ClassReview,Long>{ List<ClassReview> findByClassId(Long classId); Optional<ClassReview> findByCustomerIdAndClassId(Long customerId,Long classId); }
