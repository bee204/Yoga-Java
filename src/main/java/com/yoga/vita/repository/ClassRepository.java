package com.yoga.vita.repository;

import com.yoga.vita.entity.YogaClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassRepository extends JpaRepository<YogaClass, Long> {
    List<YogaClass> findByTeacherId(Long teacherId);
}
