package com.yoga.vita.repository; import com.yoga.vita.entity.Customer; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface CustomerRepository extends JpaRepository<Customer,Long>{ Optional<Customer> findByEmail(String email); }
