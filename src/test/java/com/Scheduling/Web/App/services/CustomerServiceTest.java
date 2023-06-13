package com.Scheduling.Web.App.services;
import com.Scheduling.Web.App.models.Customer;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerServiceTest {


    /**
     * This is a unit test for the getFilteredCustomerList method in the CustomerService class.
     * Unit test to see if the filtered list returns customers with USA as country and only ones with USA as country.
     */
    @Test // This is a unit test
    public void testGetFilteredCustomerList() {
        List<Customer> customers = getSampleCustomers();
        CustomerService customerService = new CustomerService();
        Customer filterCriteria = new Customer();
        filterCriteria.setCountry("USA");
        List<Customer> filteredCustomers = customerService.getFilteredCustomerList(filterCriteria);
        for (Customer customer : filteredCustomers) {
            assertEquals("USA", customer.getCountry());
        }
    }private static List<Customer> getSampleCustomers() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1, "Christoph", "Wonder Way", "32456",
                "111-222-3333", "Alabama", "USA"));
        customers.add(new Customer(2, "Dudley Do-Right", "Horse Manor", "44336",
                "444-555-6666", "Tennessee", "USA"));
        customers.add(new Customer(3, "Sarah Doe", "dhjkjkh", "556677",
                "444-555-6666", "Tennessee", "Canada"));
        return customers;
    }
}
