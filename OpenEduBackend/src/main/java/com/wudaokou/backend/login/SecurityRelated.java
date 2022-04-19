package com.wudaokou.backend.login;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityRelated {
    private final CustomerRepository customerRepository;

    public SecurityRelated(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomer(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return customerRepository.findByUsername(username).orElseThrow();
    }

    public Optional<Customer> getCustomer(String token){
        token = token.replaceFirst("Bearer", "").trim();
        return customerRepository.findByToken(token);
    }
}
