package com.dteknoloji.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.dteknoloji.domain.Customer;
import com.dteknoloji.service.CustomerService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Customer> getAllCustomers() {
        return service.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Customer> viewCustomer(@PathVariable String id) {
        Customer customer = service.findById(Long.valueOf(id));
        if (customer == null) {
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer restCustomer, UriComponentsBuilder builder) {

        Customer newCustomer = service.save(restCustomer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/customers/{id}").buildAndExpand(newCustomer.getIdentity().toString()).toUri());

        return new ResponseEntity<Customer>(newCustomer, headers, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable String id) {

        Customer customer = service.findById(Long.valueOf(id));
        if (customer == null) {
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        boolean deleted = service.delete(Long.valueOf(id));
        if (deleted) {
            return new ResponseEntity<Customer>(customer, HttpStatus.OK);
        }

        return new ResponseEntity<Customer>(customer, HttpStatus.FORBIDDEN);
    }

}
