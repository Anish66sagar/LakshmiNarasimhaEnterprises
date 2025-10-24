package com.example.milkdelivery.repository;

import com.example.milkdelivery.entity.Order;
import com.example.milkdelivery.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Orders for a specific date (existing)
    List<Order> findByDeliveryDate(LocalDate date);

    // Orders for a specific customer on a specific date
    List<Order> findByCustomerAndDeliveryDate(Customer customer, LocalDate deliveryDate);

    // Orders for a customer within a date range (e.g., last 30 days for monthly bill)
    List<Order> findByCustomerAndDeliveryDateBetween(Customer customer, LocalDate startDate, LocalDate endDate);

    List<Order> findByCustomerIdAndDeliveryDateBetween(Long customerId, LocalDate start, LocalDate end);


}
