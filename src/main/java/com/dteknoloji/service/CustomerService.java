package com.dteknoloji.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dteknoloji.domain.Customer;
import com.dteknoloji.repository.CustomerRepository;

@Transactional
@Service
public class CustomerService {
    @Autowired
    CustomerRepository repository;

    public boolean login(String username, String password) {
        List<Customer> user = repository.findUsersByUsernameAndPassword(username, password);
        return user.size() == 1;
    }

    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    public List<Customer> findAll() {
        List<Customer> customerList = new ArrayList<Customer>();

        for (Customer customer : repository.findAll()) {
            customerList.add(customer);
        }

        return customerList;
    }

    public Customer findById(Long id) {
        Customer customer = repository.findOne(id);

        //The below line is crucial if you have lazy type fetch
        //customer.getApplicationList().size();

        return customer;
    }

    public boolean delete(Long id) {
        repository.delete(id);
        return true;
    }

}
