package com.sai.My_Bank_Experiment.Repository;

import com.sai.My_Bank_Experiment.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Customer findByAccountNumber(Long accountNumber);

    void deleteByAccountNumber(Long accountNumber);

    boolean existsByAccountNumber(Long accountNumber);

    @Query(value = "SELECT MAX(accountNumber) FROM Customer")
    Long getLastStoredAccountNumber();

    @Query("SELECT c FROM Customer c WHERE UPPER(c.fullName) LIKE UPPER(CONCAT('%', :name, '%'))")
    List<Customer> findCustomersByNameIgnoreCase(@Param("name") String name);

    @Query(value = "SELECT c FROM Customer c WHERE c.mobileNumber = :mobileNumber")
    Customer findCustomerByMobileNumber(@Param("mobileNumber") String mobileNumber);


    @Query("SELECT c FROM Customer c WHERE c.amount = :amount")
    List<Customer> findCustomersWithAmountEqual(@Param("amount") Double amount);

    @Query("SELECT c FROM Customer c WHERE c.amount > :minAmount")
    List<Customer> findCustomersWithAmountGreaterThan(@Param("minAmount") Double minAmount);

    @Query("SELECT c FROM Customer c WHERE c.amount < :maxAmount")
    List<Customer> findCustomersWithAmountLessThan(@Param("maxAmount") Double maxAmount);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Customer c")
    Double getTotalAmountInBank();

    long count(); // total accounts

    Long countByAccountNumberIsNull(); // For total deleted accounts

    Optional<Customer> findTopByOrderByAmountDesc(); // Find customer with the highest amount

    Optional<Customer> findTopByOrderByAmountAsc(); // Find customer with the lowest amount

}


