package com.wudaokou.backend.login;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Boolean existsByUsername(String username);
    Optional<Customer> findByUsername(String username);

    Boolean existsByToken(String token);
    Optional<Customer> findByToken(String token);
}
