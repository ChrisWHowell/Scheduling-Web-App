package com.Scheduling.Web.App.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

/**
 * @author Chris Howell
 * @version 1.0
 * Represents a Customer Object in the application.
 * **
 *  The Customer class is responsible for storing all variables associated
 *  with any specific Customer. It extends the BaseEntity class to include
 *  the ID, createdDate, updatedDate, createdBy, and lastUpdatedBy variables by
 *  utilizing inheritance of the BaseEntity class instance variables and methods.
 *
 */
public class Customer extends BaseEntity {


    private String  name;

    private String address;

    private String postal_code;
    private String phone;

    private int division_id;
    private String division;

    private String country;
    private int country_ID;

    private String centralDateTime;



    /**
     * Default Constructor for creating a Customer object with no defined fields
     */
    public Customer() {
    }

    /**
     * The Customer Constructor creates a Customer object and defines the attributes associated with each customer.
     * @param id
     * @param name
     * @param address
     * @param postal_code
     * @param phone
     * @param division
     * @param country
     */
    public Customer(int id, String name, String address, String postal_code, String phone, String division, String country) {
        super(id);
        this.name = name;
        this.address = address;
        this.postal_code = postal_code;
        this.phone = phone;
        this.division = division;
        this.country = country;
    }

    public Customer(String name, String address, String postal_code, String phone, int division_id, String division, String country, int country_ID, int id) {
        super(id);
        this.name = name;
        this.address = address;
        this.postal_code = postal_code;
        this.phone = phone;
        this.division_id = division_id;
        this.division = division;
        this.country = country;
        this.country_ID = country_ID;

    }

    /**
     * Returns a string representation of the Customer object.
     *
     * @return a string representation of the Customer object
     */
    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", postal_code='" + postal_code + '\'' +
                ", phone='" + phone + '\'' +
                ", division='" + division + '\'' +
                ", country='" + country + '\'' +
                ", id=" + getId() +
                '}';
    }

    /**
     * Gets the name from the calling Customer instance variable
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the instance of the calling Customer object.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address from the calling Customer instance variable
     * @return String address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address for the instance of the calling Customer object
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the postal_code from the calling Customer instance variable
     * @return String postal_code
     */
    public String getPostal_code() {
        return postal_code;
    }

    /**
     * Sets the postal_code for the instance of the calling Customer object.
     * @param postal_code
     */
    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    /**
     * Gets the phone from the calling Customer instance variable
     * @return String phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number for the instance of the calling Customer object
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the division from the calling Customer instance variable
     * @return String division
     */
    public String getDivision() {
        return division;
    }

    /**
     * Sets the division for the instance of the calling Customer object.
     * @param division
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * Gets the country from the calling Customer instance variable
     * @return String country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country for the instance of the calling Customer object
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the Customer ID from the calling Customer instance variable
     * @return int id
     */
    public int getCountry_ID() {
        return country_ID;
    }

    public void setCountry_ID(int country_ID) {
        this.country_ID = country_ID;
    }

    public int getDivision_id() {
        return division_id;
    }

    public void setDivision_id(int division_id) {
        this.division_id = division_id;
    }

    public String getCentralDateTime() {
        return centralDateTime;
    }

    public void setCentralDateTime(String centralDateTime) {
        this.centralDateTime = centralDateTime;
    }
}