package com.example;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by ttapanai on 22/10/15.
 */
@RestController
@RequestMapping()
public class ProductController {

    private Map<String, Product> products = new HashMap<>();

    @PostConstruct
    public void init() {
        products.put("1", new Product("1", "Samsung Galaxy S4", "Mobile"));
        products.put("2", new Product("2", "iPhone 5S", "Mobile"));
    }

    @RequestMapping(value = "/products", method = GET)
    public Collection<Product> getProducts() {
        System.out.println("GET products");
        return products.values();
    }

    @RequestMapping(value = "/products/{productId}", method = GET)
    public Product getProduct(@PathVariable String productId) {
        System.out.println("GET product by id: " + productId);
        return products.get(productId);
    }

    @RequestMapping(method = HEAD)
    public void head() {
    }

    @RequestMapping(method = OPTIONS)
    public void options() {
    }
}
