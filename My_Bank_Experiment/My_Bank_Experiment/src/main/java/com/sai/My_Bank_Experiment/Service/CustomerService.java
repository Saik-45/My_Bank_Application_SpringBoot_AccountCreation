package com.sai.My_Bank_Experiment.Service;

import com.sai.My_Bank_Experiment.Entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {
    void createAccount(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerByAccountNumber(Long accountNumber);
    boolean deleteCustomerByAccountNumber(Long accountNumber);
    boolean updateCustomerByAccountNumber(Long accountNumber, Customer updatedCustomer);
    boolean existsByAccountNumber(Long accountNumber);
    @Transactional(readOnly = true)
    Long getLastStoredAccountNumber();

    List<Customer> findCustomersByName(String name);

    Customer findCustomerByMobileNumber(String mobileNumber);

    List<Customer> findCustomersWithAmountEqual(Double amount);

    List<Customer> findCustomersWithAmountGreaterThan(Double minAmount);

    List<Customer> findCustomersWithAmountLessThan(Double maxAmount);

    Double getTotalAmountInBank();

    Long getTotalAccounts();

    Long getTotalDeletedAccounts();

    Optional<Customer> findCustomerWithHighestAmount();

    Optional<Customer> findCustomerWithLowestAmount();


}
