package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by ttapanai on 22/10/15.
 */
@RestController
@RequestMapping()
public class CustomerController {

    private Map<String, Customer> customers = new HashMap<>();

    @PostConstruct
    public void init() {
        customers.put("1", new Customer("1", "Timo", "Tapanainen"));
        customers.put("2", new Customer("2", "Antti", "The Fox Kettunen"));
    }

    @RequestMapping(value = "/customers", method = GET)
    public Collection<Customer> getCustomers() {
        System.out.println("GET customers");
        return customers.values();
    }

    @RequestMapping(value = "/customers/{customerId}", method = GET)
    public Customer getCustomer(@PathVariable String customerId) {
        System.out.println("GET customer by id: " + customerId);
        return customers.get(customerId);
    }

    @RequestMapping(value = "/customers", method = POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void postCustomer(@RequestBody Customer customer) {
        customers.put(customer.getId(), customer);
    }

}
