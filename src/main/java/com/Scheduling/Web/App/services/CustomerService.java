package com.Scheduling.Web.App.services;

import com.Scheduling.Web.App.models.Customer;
import com.Scheduling.Web.App.repository.JDBCCustomerRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    public boolean updateCustomer(Customer customer,String username){
        boolean bool =false;
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();
        if(jcr.editCustomer(customer,username)){
            bool= true;
        }
        return bool;
    }
    public ArrayList<String> getCountries(){
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();
        return jcr.getCountryNames();
    }
    public ArrayList<String> getDivisions(String countryName){
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();
        return jcr.getDivisionNames(countryName);
    }
    public ArrayList<Customer> getCustomers(){
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();
        return jcr.getCustomers();
    }
    public boolean createNewCustomer(Customer customer,String username){
        boolean bool =false;
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();

        if(jcr.newCustomer(customer,username)){
            bool= true;
        }
        return bool;
    }
    public ArrayList<Customer> getFilteredCustomerList(Customer customer) {
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();
        return jcr.getFilteredCustomerList(customer);
    }
    public boolean deleteCustomer(Customer customer){
        boolean bool =false;
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();
        if(jcr.deleteCustomer(customer)){
            bool= true;
        }
        return bool;
    }
    public ArrayList<Integer> getCustomerIds(){
        JDBCCustomerRepository jcr = new JDBCCustomerRepository();
        return jcr.getCustomerIds();
    }
}
