package com.yoga.vita.repository;

import com.yoga.vita.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByIdempotencyKey(String idempotencyKey);
    List<Registration> findByCustomerId(Long customerId);
    List<Registration> findByCustomerIdAndStatusIn(Long customerId, List<String> statuses);
    Optional<Registration> findByCustomerIdAndClassIdAndStatusIn(Long customerId, Long classId, List<String> statuses);
    List<Registration> findByClassIdAndStatus(Long classId, String status);
    List<Registration> findByClassId(Long classId);
    long countByStatus(String status);
    long countByClassIdAndStatus(Long classId, String status);
    void deleteByClassId(Long classId);
    void deleteByCustomerId(Long customerId);
}
