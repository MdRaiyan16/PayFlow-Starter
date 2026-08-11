package com.payflow.gateway.customer.repository;

import com.payflow.gateway.auth.entity.User;
import com.payflow.gateway.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /*
     * Find customer using the associated User.
     */
    Optional<Customer> findByUser(User user);

    /*
     * Find customer using the User's ID.
     */
    Optional<Customer> findByUserId(Long userId);

    /*
     * Check whether a customer already exists
     * for a particular User.
     */
    boolean existsByUser(User user);

    /*
     * Check whether a customer already exists
     * for a particular User ID.
     */
    boolean existsByUserId(Long userId);
}