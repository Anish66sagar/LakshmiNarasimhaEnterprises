package com.example.milkdelivery.controller;

import com.example.milkdelivery.entity.Order;
import com.example.milkdelivery.entity.Customer;
import com.example.milkdelivery.repository.CustomerRepository;
import com.example.milkdelivery.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;
    private final CustomerRepository customerRepository;

    public OrderController(OrderService orderService, CustomerRepository customerRepository) {
        this.orderService = orderService;
        this.customerRepository = customerRepository;
    }

    // -------------------------
    // Place new order
    // -------------------------
    @PostMapping("/customers/{id}/orders")
    public ResponseEntity<?> placeOrder(@PathVariable Long id,
                                        @RequestBody Order order,
                                        Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized: No authentication provided");
        }

        String authPhone = auth.getName();
        Optional<Customer> cOpt = customerRepository.findById(id);
        if (cOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        Customer c = cOpt.get();
        if (!authPhone.equals(c.getPhone())) {
            return ResponseEntity.status(403)
                    .body("Forbidden: You can only place orders for your own account");
        }

        Order saved = orderService.placeOrder(id, order);
        return ResponseEntity.ok(saved);
    }

    // -------------------------
    // Get all orders for a given date (admin or generic use)
    // -------------------------
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrdersByDate(@RequestParam String date) {
        List<Order> orders = orderService.getOrdersByDate(LocalDate.parse(date));
        return ResponseEntity.ok(orders);
    }

    // -------------------------
    // Get logged-in customer's orders between dates
    // -------------------------
    @GetMapping("/orders/customer")
    public ResponseEntity<List<Order>> getOrdersForCustomer(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        String authPhone = auth.getName();
        Optional<Customer> customerOpt = customerRepository.findByPhone(authPhone);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Customer customer = customerOpt.get();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<Order> orders = orderService.getOrdersForCustomerBetweenDates(customer.getId(), start, end);
        return ResponseEntity.ok(orders);
    }
}
