package com.example.milkdelivery.service;

import com.example.milkdelivery.entity.Customer;
import com.example.milkdelivery.entity.Order;
import com.example.milkdelivery.repository.CustomerRepository;
import com.example.milkdelivery.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    // -------------------------
    // Place order
    // -------------------------
    public Order placeOrder(Long customerId, Order order) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        order.setCustomer(customerOpt.get());

        // Default delivery date to today if not set
        if (order.getDeliveryDate() == null) {
            order.setDeliveryDate(LocalDate.now());
        }

        return orderRepository.save(order);
    }

    // -------------------------
    // Get all orders by specific date
    // -------------------------
    public List<Order> getOrdersByDate(LocalDate date) {
        return orderRepository.findByDeliveryDate(date);
    }

    // -------------------------
    // Get a customer's today's orders
    // -------------------------
    public List<Order> getOrdersByCustomerAndDate(Long customerId, LocalDate date) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        return orderRepository.findByCustomerAndDeliveryDate(customerOpt.get(), date);
    }

    // -------------------------
    // Get a customer's orders within a date range (monthly bill cycle)
    // -------------------------
    public List<Order> getOrdersBetweenDates(Long customerId, LocalDate startDate, LocalDate endDate) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        return orderRepository.findByCustomerAndDeliveryDateBetween(customerOpt.get(), startDate, endDate);
    }

    // -------------------------
    // Get single order (optional existing)
    // -------------------------
    public Optional<Order> getOrdersByCustomerID(Long id) {
        return orderRepository.findById(id);
    }
    public List<Order> getOrdersForCustomerBetweenDates(Long customerId, LocalDate start, LocalDate end) {
        return orderRepository.findByCustomerIdAndDeliveryDateBetween(customerId, start, end);
    }

}
