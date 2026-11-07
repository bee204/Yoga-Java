package com.yoga.vita.repository;

import com.yoga.vita.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByRegistrationId(Long registrationId);
}
