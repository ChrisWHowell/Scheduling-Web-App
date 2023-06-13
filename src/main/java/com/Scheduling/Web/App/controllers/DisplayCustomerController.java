package com.Scheduling.Web.App.controllers;

import com.Scheduling.Web.App.models.Customer;
import com.Scheduling.Web.App.security.ForbiddenException;
import com.Scheduling.Web.App.services.CustomerService;
import com.Scheduling.Web.App.services.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is a controller responsible for handling various customer-related operations such as
 * fetching customer lists, adding new customers, editing existing customers, deleting customers,
 * and applying filters to customer records. It receives requests from the front-end, processes
 * the incoming data, and interacts with the CustomerService class to perform the desired operations.
 * The results of these operations are then sent back to the front-end in the form of appropriate
 * HTTP responses or JSON data.
 */
@Controller
public class DisplayCustomerController {

    /**
     * This method is responsible for displaying the 'displayCustomers' page to the user. It is called whenever the user
     * navigates to the '/displayCustomers' URL. Additionally, if the user has just successfully logged in and navigated to
     * this page directly from the login page, this method will log the successful login attempt.
     *
     * To determine whether the user has come from the login page, the method checks the 'Referer' HTTP header of the request.
     * If the 'Referer' header contains the login URL, it is assumed that the user has just logged in. In such a case, the
     * user's authentication object is checked to ensure they are authenticated, and then the 'logLoginAttempt' method is
     * called with the appropriate parameters to log the successful login attempt.
     *
     * Note: This method of determining if the user has just logged in is not foolproof, as the 'Referer' header can be
     * manipulated or disabled by the user or browser. However, it should work in most scenarios.
     *
     * @param authentication the user's authentication object, provided by Spring Security
     * @param model the model object used to pass data to the view
     * @param request the HttpServletRequest object, which is used to access the 'Referer' header
     * @return the name of the 'displayCustomers' view to be rendered
     */
    @GetMapping("/displayCustomers")
    public String displayCustomers(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null && authentication.isAuthenticated()) {
            String referer = request.getHeader("Referer");
            if (referer != null && referer.contains("/login")) {
                String username = authentication.getName();
                UserDetailsServiceImpl.logLoginAttempt(username, true);
            }
        }

        return "displayCustomers";
    }

    /** ***        *********Polymorphism Example*******
     * This method is responsible for updating a customer's information in the system. It is called when the user clicks
     * the 'Save as edit' button in the front-end. The method receives a Customer object containing the updated information,
     * as well as the user's authentication object. This method is mapped to the '/api/saveAsEdit' URL.
     *
     * This method implements polymorphism by changing the return type of authentication.getPrincipal() from Object to
     * UserDetails. Since the UserDetails is an interface, it allows the object to use polymorphism once again
     * based on its implementation. In this case, the UserDetails interface is implemented by the User class,
     * through the getUsername method allowing the method to retrieve the username of the currently authenticated user.
     *
     * The updated Customer object and the username are passed to the 'updateCustomer' method of the CustomerService class.
     * This method processes the Customer object, updates the relevant customer record in the database, and returns a
     * boolean value indicating whether the update was successful or not.
     *
     * If the update was successful, the method returns a 'Success' message to the front-end. Otherwise, it returns an
     * 'Error' message, indicating that the update operation has failed.
     *
     * @param customer the Customer object containing the updated information, received from the front-end as a JSON object
     * @param authentication the user's authentication object, provided by Spring Security
     * @return a string containing either 'Success' or 'Error', depending on the result of the update operation
     */
    @PostMapping("/api/saveAsEdit")
    public @ResponseBody String saveAsEdit(@RequestBody Customer customer, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        CustomerService customerService = new CustomerService();

        boolean result = customerService.updateCustomer(customer,username);
        // Return a success message or error message
        if (result) {
            return "Success";
        } else {
            return "Error";
        }
    }

    /**
     * This method is responsible for adding a new customer to the system. It is called when the user clicks
     * the 'save as new' button in the front-end. The method receives a Customer object containing the new customer's
     * information, as well as the user's authentication object. The method is mapped to the '/api/saveAsNew' URL.
     *
     * The method first retrieves the username of the currently authenticated user from the authentication object. Then,
     * it creates a new instance of the CustomerService class, which is responsible for handling customer-related operations.
     *
     * The Customer object and the username are passed to the 'createNewCustomer' method of the CustomerService class.
     * This method processes the Customer object, inserts a new customer record into the database, and returns a
     * boolean value indicating whether the creation was successful or not.
     *
     * If the creation was successful, the method returns a 'Success' message to the front-end. Otherwise, it returns an
     * 'Error' message, indicating that the creation operation has failed.
     *
     * @param customer the Customer object containing the new customer's information, received from the front-end as a JSON object
     * @param authentication the user's authentication object, provided by Spring Security
     * @return a string containing either 'Success' or 'Error', depending on the result of the creation operation
     */
    @PostMapping("/api/saveAsNew")
    public @ResponseBody String saveAsNew(@RequestBody Customer customer, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Saving as an New");
        CustomerService customerService = new CustomerService();

        boolean result = customerService.createNewCustomer(customer,username);

        // Return a success message or error message
        if (result) {
            return "Success";
        } else {
            return "Error";
        }
    }

    /**
     * This method is responsible for retrieving a list of countries from the database. It is called when the
     * front-end requests the list of available countries. The method is mapped to the '/api/countries' URL.
     *
     * The method creates a new instance of the CustomerService class, which is responsible for handling
     * customer-related operations. The 'getCountries' method of the CustomerService class is then called,
     * which retrieves the list of countries from the database.
     *
     * The method returns the list of countries as a JSON object to the front-end, where it can be used to
     * populate the UI elements as needed.
     *
     * @return a list of country names, retrieved from the database
     */
    @GetMapping("/api/countries")
    @ResponseBody
    public List<String> getCountries() {
        CustomerService customerService = new CustomerService();
        return customerService.getCountries();
    }
    /**
     * This method is responsible for retrieving a list of divisions within a specified country from the database.
     * It is called when the front-end requests the list of available divisions for a selected country, It is mapped
     * to the '/api/divisions' URL.
     *
     * The method takes a 'countryName' as a parameter, which is used to filter the divisions in the database.
     *
     * The method creates a new instance of the CustomerService class, which is responsible for handling
     * customer-related operations. The 'getDivisions' method of the CustomerService class is then called,
     * passing the 'countryName' parameter, which retrieves the list of divisions for the specified country
     * from the database.
     *
     * The method returns the list of divisions as a JSON object to the front-end, where it can be used to
     * populate the UI elements as needed.
     *
     * @param countryName the name of the country for which divisions should be retrieved
     * @return a list of division names within the specified country, retrieved from the database
     */
    @GetMapping("/api/divisions")
    @ResponseBody
    public List<String> getDivisions(String countryName) {
        CustomerService customerService = new CustomerService();
        return customerService.getDivisions(countryName);
    }

    /**
     * This method is responsible for retrieving a list of all customers from the database.
     * It is called when the front-end requests the list of customers for displaying, searching,
     * or filtering in the user interface.
     *
     * The method creates a new instance of the CustomerService class, which is responsible for handling
     * customer-related operations. The 'getCustomers' method of the CustomerService class is then called,
     * which retrieves the list of all customers from the database.
     *
     * The method returns the list of customers as a JSON object to the front-end, where it can be used to
     * populate tables, lists, or other UI elements as needed.
     *
     * @return a list of all customers retrieved from the database
     */
    @GetMapping("/api/customer_List")
    @ResponseBody
    public List<Customer> getCustomerList() {
        CustomerService customerService = new CustomerService();
        return customerService.getCustomers();
    }

    /**
     * This method applies filters to the list of customers and returns a filtered list based on the
     * provided criteria. The front-end sends a Customer object containing the filter criteria, such as
     * name, address, or other customer fields.
     *
     * The method creates a new instance of the CustomerService class, which is responsible for handling
     * customer-related operations. The 'getFilteredCustomerList' method of the CustomerService class is
     * then called, passing the filter criteria in the form of a Customer object.
     *
     * The method returns the filtered list of customers as a JSON object to the front-end, where it can
     * be used to update tables, lists, or other UI elements to display only the customers that match the
     * applied filters.
     *
     * @param customer a Customer object containing the filter criteria for the customer list
     * @return a filtered list of customers based on the provided criteria
     */
    @PostMapping("/api/applyFilter")
    @ResponseBody
    public List<Customer> applyFilter(@RequestBody Customer customer) {
        System.out.println("filter has reached the java");
        CustomerService customerService = new CustomerService();
        ArrayList<Customer> filteredCustomers= customerService.getFilteredCustomerList(customer);
        filteredCustomers.forEach(customer1 -> System.out.println(customer1.toString()));
        return customerService.getFilteredCustomerList(customer);
    }



    @PostMapping("/api/deleteCustomer")
    public @ResponseBody String deleteCustomer(@RequestBody Customer customer, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String username = authentication.getName();
        System.out.println("Username: " + username);
        System.out.println("Authorities: " + authorities);
        // Check if the user has the required role
        boolean hasRequiredRole = authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        if (!hasRequiredRole) {
            System.out.println("Does not have required role");
            throw new ForbiddenException("You do not have the correct permissions to do this action.");
        }
        System.out.println("deleting customer");
        CustomerService customerService = new CustomerService();
        boolean result = customerService.deleteCustomer(customer);
        System.out.println("Customer Deleted Attempt Result: " + result);
        // Return a success message or error message
        if (result) {
            return "Success";
        } else {
            return "Error";
        }
    }
}