package com.yoga.vita.repository; import com.yoga.vita.entity.Registration; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface RegistrationRepository extends JpaRepository<Registration,Long>{ List<Registration> findByCustomerId(Long customerId); List<Registration> findByClassId(Long classId); Optional<Registration> findByIdempotencyKey(String key); }
