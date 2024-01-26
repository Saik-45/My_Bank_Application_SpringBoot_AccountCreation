package com.sai.My_Bank_Experiment.Controller;


import com.sai.My_Bank_Experiment.Entity.Customer;
import com.sai.My_Bank_Experiment.Service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/bank")
public class CustomerController {

    private final CustomerService customerService;

    private static final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());

    public CustomerController(CustomerService customerService) {

        this.customerService = customerService;
    }


    @GetMapping("/home")
    public String home() {
        LOGGER.info("Endpoint: /bank/home accessed.");
        return "Home Of Bank-Application";
    }

    @PostMapping("/new")
    public ResponseEntity<?> createAccount(@Validated @RequestBody Customer customer) {
        LOGGER.info("Endpoint: /bank/new accessed. Creating a new customer.");

        Customer existingCustomer = customerService.getCustomerByAccountNumber(customer.getAccountNumber());

        if (existingCustomer != null) {
            LOGGER.warning("You Entered Account already exists in the database: " + customer.getAccountNumber());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You Entered Account Number already exists in the database.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        customer.setAccountOpenDate(currentDateTime.format(formatter));

        customerService.createAccount(customer);

        LOGGER.info("Account Created Successfully...");

        return ResponseEntity.status(HttpStatus.CREATED).body("Account Created Successfully...\nYour Account Number : " + customer.getAccountNumber());
    }

    @GetMapping("/all")
    public ResponseEntity<String> getAllCustomers() {
        LOGGER.info("Endpoint: /bank/All accessed. Retrieving all customers.");
        List<Customer> customers = customerService.getAllCustomers();

        StringBuilder response = new StringBuilder();

        if (customers.isEmpty()) {
            response.append("\t\tNo Accounts In a Database...");
        } else {
             response = new StringBuilder("\t\tExisted  Accounter's Details In A DataBase\n");

            for (Customer customer : customers) {
                response.append("\n- Customer ID       : ").append(customer.getId()+"\n")
                        .append("- Full Name         : ").append(customer.getFullName()+"\n")
                        .append("- Account Number    : ").append(customer.getAccountNumber()+"\n")
                        .append("- Mobile Number     : ").append(customer.getMobileNumber()+"\n")
                        .append("- Date of Birth     : ").append(customer.getDob()+"\n")
                        .append("- Account Open Date : ").append(customer.getAccountOpenDate()+"\n")
                        .append("- ATM PIN           : ").append(customer.getAtmPin()+"\n")
                        .append("- Amount            : ").append(customer.getAmount()+"\n\n")
                        .append("-----------------------------------------------------\n\n");
            }
        }

        return ResponseEntity.ok(response.toString());
    }



    @GetMapping("/get/{accountNumber}")
    public ResponseEntity<?> getCustomerByAccountNumber(@PathVariable Long accountNumber) {
        LOGGER.info("Endpoint: /bank/get/{accountNumber} accessed. Retrieving customer by account number: " + accountNumber);
        Customer customer = customerService.getCustomerByAccountNumber(accountNumber);

        if (customer != null) {
            LOGGER.info("Customer found for account number: " + accountNumber);
            return ResponseEntity.ok(customer);
        } else {
            LOGGER.warning("Customer not found for account number: " + accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entered Account Number Not found In Database : " + accountNumber);
        }
    }

    @DeleteMapping("/delete/{accountNumber}")
    public ResponseEntity<String> deleteCustomerByAccountNumber(@PathVariable Long accountNumber) {
        LOGGER.info("Endpoint: /bank/delete/" + accountNumber + " accessed. Deleting customer by account number.");

        boolean customerExists = customerService.existsByAccountNumber(accountNumber);

        if (!customerExists) {
            LOGGER.warning("Customer not found for deletion with account number: " + accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("You entered Account Number Does Not Exist in the database: " + accountNumber);
        }

        boolean deleted = customerService.deleteCustomerByAccountNumber(accountNumber);

        if (deleted) {
            LOGGER.info("Customer deleted successfully for account number: " + accountNumber);
            return ResponseEntity.ok("\t\t"+ accountNumber+" Account deleted Successfully...");
        } else {
            LOGGER.warning("Failed to delete customer for account number: " + accountNumber);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete customer. Please try again later...");
        }
    }

    @PutMapping("/update/{accountNumber}")
    public ResponseEntity<String> updateCustomerByAccountNumber(
            @PathVariable Long accountNumber,
            @Validated @RequestBody Customer updatedCustomer
    ) {
        LOGGER.info("Endpoint: /bank/update/" + accountNumber + " Accessed. Updated Customer By Account Number...");

        boolean updated = customerService.updateCustomerByAccountNumber(accountNumber, updatedCustomer);

        if (updated) {
            LOGGER.info("Customer Updated Successfully for Account Number: " + accountNumber);
            return ResponseEntity.ok("\t\t Customer updated successfully...");
        } else {
            LOGGER.warning("Customer not found for updating with account number: " + accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("\t" + accountNumber + " Account Number Does Not Exist In DataBase...");
        }
    }


    @GetMapping("/LastAccountNumberInBd")
    public ResponseEntity<String> getLastStoredAccountNumber() {
        LOGGER.info("Endpoint: /bank/lastStoredAccountNumber accessed. Retrieving last stored account number.");
        Long lastStoredAccountNumber = customerService.getLastStoredAccountNumber();
        return ResponseEntity.ok("Last Generated Account Number In DataBase : "+lastStoredAccountNumber);
    }

    @GetMapping("/findByName")
//       http://localhost:4545/bank/findByName?name=sai
    public ResponseEntity<?> findCustomersByName(@RequestParam String name) {
        try {
            List<Customer> customers = customerService.findCustomersByName(name);

            if (customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customers found with Name : " + name);
            }

            StringBuilder response = new StringBuilder();
            response.append("Details Of Entered Name : ").append(name).append("\n");

            for (Customer customer : customers) {
                response.append("\n- Customer ID       : ").append(customer.getId()+"\n")
                        .append("- Full Name         : ").append(customer.getFullName()+"\n")
                        .append("- Account Number    : ").append(customer.getAccountNumber()+"\n")
                        .append("- Mobile Number     : ").append(customer.getMobileNumber()+"\n")
                        .append("- Date of Birth     : ").append(customer.getDob()+"\n")
                        .append("- Account Open Date : ").append(customer.getAccountOpenDate()+"\n")
                        .append("- ATM PIN           : ").append(customer.getAtmPin()+"\n")
                        .append("- Amount            : ").append(customer.getAmount()+"\n\n")
                        .append("-----------------------------------------------------\n\n");
            }

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/findByMobileNumber")
//            http://localhost:4545/bank/findByMobileNumber?mobileNumber=9603945574
    public ResponseEntity<?> findCustomerByMobileNumber(@RequestParam String mobileNumber) {
        try {
            Customer customer = customerService.findCustomerByMobileNumber(mobileNumber);

            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customer found with mobile number: " + mobileNumber);
            }

            StringBuilder response = new StringBuilder();
            response.append(mobileNumber+" Mobile Number Account Details \n\n")
                    .append("- Customer ID       : ").append(customer.getId()).append("\n")
                    .append("- Full Name         : ").append(customer.getFullName()).append("\n")
                    .append("- Account Number    : ").append(customer.getAccountNumber()).append("\n")
                    .append("- Mobile Number     : ").append(customer.getMobileNumber()).append("\n")
                    .append("- Date of Birth     : ").append(customer.getDob()).append("\n")
                    .append("- Account Open Date : ").append(customer.getAccountOpenDate()).append("\n")
                    .append("- ATM PIN           : ").append(customer.getAtmPin()).append("\n")
                    .append("- Amount            : ").append(customer.getAmount()).append("\n\n")
                    .append("-----------------------------------------------------\n\n");

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }



    @GetMapping("/ae/{amount}")
    public ResponseEntity<?> getCustomersWithAmountEqual(@PathVariable Double amount) {
        try {

            List<Customer> customers = customerService.findCustomersWithAmountEqual(amount);

            if (customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customers found with amount equal to: " + amount);
            }

            StringBuilder response = new StringBuilder();
            response.append("Amount Equals to ").append(amount).append("\n");
            response.append("Total Accounts : ").append(customers.size()).append("\n");

            for (Customer customer : customers) {

                response.append("\n- Customer ID       : ").append(customer.getId()+"\n")
                        .append("- Full Name         : ").append(customer.getFullName()+"\n")
                        .append("- Account Number    : ").append(customer.getAccountNumber()+"\n")
                        .append("- Mobile Number     : ").append(customer.getMobileNumber()+"\n")
                        .append("- Date of Birth     : ").append(customer.getDob()+"\n")
                        .append("- Account Open Date : ").append(customer.getAccountOpenDate()+"\n")
                        .append("- ATM PIN           : ").append(customer.getAtmPin()+"\n")
                        .append("- Amount            : ").append(customer.getAmount()+"\n\n")
                        .append("-----------------------------------------------------\n\n");
            }

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/ag/{minAmount}")
    public ResponseEntity<?> getCustomersWithAmountGreaterThan(@PathVariable Double minAmount) {
        try {
            List<Customer> customers = customerService.findCustomersWithAmountGreaterThan(minAmount);

            if (customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customers found with amount greater than : " + minAmount);
            }

            StringBuilder response = new StringBuilder();
            response.append(" Amount Greater than ").append(minAmount).append("\n");
            response.append("Total Accounts : ").append(customers.size()).append("\n");

            for (Customer customer : customers) {
                response.append("\n- Customer ID       : ").append(customer.getId()+"\n")
                        .append("- Full Name         : ").append(customer.getFullName()+"\n")
                        .append("- Account Number    : ").append(customer.getAccountNumber()+"\n")
                        .append("- Mobile Number     : ").append(customer.getMobileNumber()+"\n")
                        .append("- Date of Birth     : ").append(customer.getDob()+"\n")
                        .append("- Account Open Date : ").append(customer.getAccountOpenDate()+"\n")
                        .append("- ATM PIN           : ").append(customer.getAtmPin()+"\n")
                        .append("- Amount            : ").append(customer.getAmount()+"\n\n")
                        .append("-----------------------------------------------------\n\n");
            }

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    @GetMapping("/al/{maxAmount}")
    public ResponseEntity<?> getCustomersWithAmountLessThan(@PathVariable Double maxAmount) {
        try {
            List<Customer> customers = customerService.findCustomersWithAmountLessThan(maxAmount);

            if (customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customers found with amount less than: " + maxAmount);
            }

            StringBuilder response = new StringBuilder();
            response.append("Amount Less than ").append(maxAmount).append("\n");
            response.append("Total Accounts : ").append(customers.size()).append("\n");

            for (Customer customer : customers) {
                response.append("\n- Customer ID       : ").append(customer.getId()+"\n")
                        .append("- Full Name         : ").append(customer.getFullName()+"\n")
                        .append("- Account Number    : ").append(customer.getAccountNumber()+"\n")
                        .append("- Mobile Number     : ").append(customer.getMobileNumber()+"\n")
                        .append("- Date of Birth     : ").append(customer.getDob()+"\n")
                        .append("- Account Open Date : ").append(customer.getAccountOpenDate()+"\n")
                        .append("- ATM PIN           : ").append(customer.getAtmPin()+"\n")
                        .append("- Amount            : ").append(customer.getAmount()+"\n\n")
                        .append("-----------------------------------------------------\n\n");
            }

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    @GetMapping("/totalAmountInBank")
    public ResponseEntity<?> getTotalAmountInBank() {
        try {
            Double totalAmount = customerService.getTotalAmountInBank();
            return ResponseEntity.ok("Total Amount in Bank : " + totalAmount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/totalAccounts")
    public ResponseEntity<?> getTotalAccounts() {
        try {
            Long totalAccounts = customerService.getTotalAccounts();
            return ResponseEntity.ok("Total Accounts in Bank : " + totalAccounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/totalDeletedAccounts")
    public ResponseEntity<?> getTotalDeletedAccounts() {
        try {
            Long totalDeletedAccounts = customerService.getTotalDeletedAccounts();
            return ResponseEntity.ok("Total Deleted Accounts in Bank : " + totalDeletedAccounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/highestAmountInBank")
    public ResponseEntity<?> getCustomerWithHighestAmount() {
        try {
            Optional<Customer> customer = customerService.findCustomerWithHighestAmount();

            if (customer.isPresent()) {
                return ResponseEntity.ok(customer.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customers found in the bank.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/lowestAmountInBank")
    public ResponseEntity<?> getCustomerWithLowestAmount() {
        try {
            Optional<Customer> customer = customerService.findCustomerWithLowestAmount();

            if (customer.isPresent()) {
                return ResponseEntity.ok(customer.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customers found in the bank.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}
