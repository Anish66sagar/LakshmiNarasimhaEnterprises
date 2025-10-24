package com.example.milkdelivery.service;

import com.example.milkdelivery.entity.Customer;
import com.example.milkdelivery.repository.CustomerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary // mark this as the default UserDetailsService
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + phone));
        return org.springframework.security.core.userdetails.User
                .withUsername(customer.getPhone())
                .password(customer.getPasswordHash())
                .roles("USER")
                .build();
    }
}
