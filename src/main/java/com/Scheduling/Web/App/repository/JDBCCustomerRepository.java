package com.Scheduling.Web.App.repository;

import com.Scheduling.Web.App.models.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class JDBCCustomerRepository {

    /**
     * Gets the Country ID from the database associated with the Country Name
     *
     * @param country
     * @return int CountryId
     * @throws SQLException
     */
    public int getCountryIdbyCountryName(String country) throws SQLException {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        String query = "SELECT Country_ID FROM countries WHERE Country=?";
        PreparedStatement statement = connect.prepareStatement(query);
        statement.setString(1, country);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int x = resultSet.getInt("Country_ID");
            close3Connections(statement, connect, resultSet);
            return x;

        } else {
            close3Connections(statement, connect, resultSet);
            return -1; // Country not found
        }

    }

    /**
     * The getCustomers method is responsible for getting all the Customer data from the database. It creates a Customer
     * object for each row in the table and the adds each object to a ObservableList. Once completed it returns the list.
     *
     * @return ObservableList<Customer>
     */
    public ArrayList<Customer> getCustomers() {
        Statement stmt = null;
        ResultSet rs = null;
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        // retrieve data from the customers, first_level_divisions, and countries tables and join them
        String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, first_level_divisions.Division, countries.Country "
                + "FROM customers "
                + "INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID "
                + "INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";
        try {
            // execute the query and return the results as an ObservableList
            stmt = connect.createStatement();
            rs = stmt.executeQuery(query);
            ArrayList<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                customers.add(new Customer(rs.getInt(("Customer_ID")), rs.getString("Customer_Name"), rs.getString("Address"),
                        rs.getString("Postal_Code"), rs.getString("Phone"), rs.getString("Division"), rs.getString("Country")));
            }
            close3Connections(stmt, connect, rs);
            return customers;
        } catch (SQLException e) {

            e.printStackTrace();
            close3Connections(stmt, connect, rs);
            return null;
        }
    }

    public Boolean editCustomer(Customer customer, String user_name) {
        Connection connect = null;
        PreparedStatement stmt = null;
        try {
            DatabaseConnection dBC = new DatabaseConnection();
            connect = dBC.getConnection();
            String divisionName = customer.getDivision();
            int divisionID = getDivisionIDwithName(divisionName);

            stmt = connect.prepareStatement("UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                    "Last_Update = ?," + " Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?");
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPostal_code());
            stmt.setString(4, customer.getPhone());
            stmt.setTimestamp(5, Timestamp.valueOf(customer.getCentralDateTime()));
            stmt.setString(6, user_name);
            stmt.setInt(7, divisionID);
            stmt.setInt(8, customer.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 1) {
                close2Connections(stmt, connect);
                return true;

            } else {
                close2Connections(stmt, connect);
                return false;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("There was a problem in the handleSaveAsEdit(Customer customer) ");
            close2Connections(stmt, connect);
        }
        return false;
    }

    public boolean newCustomer(Customer customer, String user_Name) {
        PreparedStatement stmt = null;
        Connection connect = null;
        try {
            DatabaseConnection dBC = new DatabaseConnection();
            connect = dBC.getConnection();
            String divisionName = customer.getDivision();
            int divisionID = getDivisionIDwithName(divisionName);


            stmt = connect.prepareStatement(
                    "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPostal_code());
            stmt.setString(4, customer.getPhone());
            stmt.setTimestamp(5, Timestamp.valueOf(customer.getCentralDateTime()));
            stmt.setString(6, user_Name);
            stmt.setTimestamp(7, Timestamp.valueOf(customer.getCentralDateTime()));
            stmt.setString(8, user_Name);
            stmt.setInt(9, divisionID);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 1) {
                close2Connections(stmt, connect);
                return true;

            } else {
                close2Connections(stmt, connect);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            close2Connections(stmt, connect);
        }
        return false;
    }

    /*public int getDivisionIDwithName(String division_Name){
        int division_id = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            DatabaseConnection db = new DatabaseConnection();
            conn = db.getConnection();
            String query = "Select Division_ID from first_level_divisions " +
                    "Where Division = ?";
            stmt =conn.prepareStatement(query);
            stmt.setString(1,division_Name);
            rs = stmt.executeQuery();
            while(rs.next()){
                division_id = rs.getInt("Division_ID");
            }
            close3Connections(stmt,conn,rs);

        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        return division_id;
    }*/
    public int getDivisionIDwithName(String division_Name) {
        int division_id = 2;
        String query = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, division_Name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    division_id = rs.getInt("Division_ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return division_id;
    }

    public int getCountryIDbyDivName(String division_name) {
        int countryID = 0;
        PreparedStatement statement = null;
        Connection connect = null;
        ResultSet rs = null;

        try {
            DatabaseConnection dBC = new DatabaseConnection();
            connect = dBC.getConnection();
            String query = "Select Country_ID FROM first_level_divisions WHERE Division = ?";
            statement = connect.prepareStatement(query);
            statement.setString(1, division_name);
            rs = statement.executeQuery();

            while (rs.next()) {
                countryID = rs.getInt("Country_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Problem in getCountryID method ");
        }
        return countryID;
    }

    public String getCountryName(int CountryID) {
        String countryName = "";
        PreparedStatement stmt = null;
        Connection connect = null;
        ResultSet rs = null;
        int division_ID = 0;
        try {
            DatabaseConnection dBC = new DatabaseConnection();
            connect = dBC.getConnection();
            String query = "Select Country FROM countries WHERE Country_ID = ?";
            stmt.setInt(1, CountryID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                countryName = rs.getString("Country");
            }
            close3Connections(stmt, connect, rs);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Problem in getCountryName method ");
        }
        return countryName;
    }

    public ArrayList<String> getDivisionNames(String countryName) {
        PreparedStatement stmt = null;
        Connection connect = null;
        ResultSet rs = null;
        ArrayList<String> divNames = new ArrayList<>();
        try {
            DatabaseConnection dBC = new DatabaseConnection();

            connect = dBC.getConnection();
            stmt = connect.prepareStatement("SELECT first_level_divisions.Division " +
                    "FROM first_level_divisions " +
                    "INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID " +
                    "WHERE countries.Country = ? ");
            stmt.setString(1, countryName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                divNames.add(rs.getString("Division"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divNames;
    }

    public ArrayList<String> getCountryNames() {
        PreparedStatement stmt = null;
        Connection connect = null;
        ResultSet rs = null;
        ArrayList<String> countryNames = new ArrayList<>();
        try {
            DatabaseConnection dBC = new DatabaseConnection();

            connect = dBC.getConnection();
            stmt = connect.prepareStatement("SELECT Country from countries");
            rs = stmt.executeQuery();
            while (rs.next()) {
                countryNames.add(rs.getString("Country"));
            }
            close3Connections(stmt, connect, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countryNames;
    }

    private void close2Connections(Statement stmt, Connection conn) {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Trouble closing 2 connections");
        }
    }

    private void close3Connections(Statement stmt, Connection conn, ResultSet rs) {
        try {
            stmt.close();
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Trouble closing 2 connections");
        }
    }

    public ArrayList<Customer> getFilteredCustomerList(Customer customer) {
        ArrayList<Customer> customerList = new ArrayList();
        Connection connect = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT * FROM customers " +
                "JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID " +
                "JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID " +
                "WHERE ";

        boolean hasPreviousFilter = false;

        if (customer.getCountry() != null && !customer.getCountry().isEmpty()) {
            query += "countries.Country = '" + customer.getCountry() + "'";
            hasPreviousFilter = true;
        }

        if (customer.getDivision() != null && !customer.getDivision().isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "first_level_divisions.Division = '" + customer.getDivision() + "'";
            hasPreviousFilter = true;
        }

        if (customer.getPostal_code() != null && !customer.getPostal_code().isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Postal_Code = '" + customer.getPostal_code() + "'";
            hasPreviousFilter = true;
        }

        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Phone = '" + customer.getPhone() + "'";
            hasPreviousFilter = true;
        }

        if (customer.getName() != null && !customer.getName().isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Customer_Name = '" + customer.getName() + "'";
            hasPreviousFilter = true;
        }

        if (customer.getAddress() != null && !customer.getAddress().isEmpty()) {
            if (hasPreviousFilter) {
                query += " AND ";
            }
            query += "Address = '" + customer.getAddress() + "'";
        }
        // Execute the query and update the table view
        try {
            DatabaseConnection db = new DatabaseConnection();
            connect = db.getConnection();

            stmt = connect.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                customerList.add(new Customer(rs.getInt(("Customer_ID")), rs.getString("Customer_Name"), rs.getString("Address"),
                        rs.getString("Postal_Code"), rs.getString("Phone"), rs.getString("Division"), rs.getString("Country")));
            }
            close3Connections(stmt, connect, rs);
            return customerList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    public boolean deleteCustomer(Customer customer) {
        boolean bool = false;
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        // Get the customer ID from the lb_custID label

        // Check if there are appointments for this customer
        boolean hasAppointments = false;
        try {
            PreparedStatement appointmentQuery = connect.prepareStatement(
                    "SELECT * FROM appointments WHERE Customer_ID = ?"
            );
            appointmentQuery.setInt(1, customer.getId());
            ResultSet appointmentResult = appointmentQuery.executeQuery();
            hasAppointments = appointmentResult.next();
        } catch (SQLException e) {
            // Display an error message and return
            e.printStackTrace();
            return bool;
        }

        // If there are appointments, ask the user if they want to proceed
        if (hasAppointments) {
            // Delete all appointments for this customer
            try {
                PreparedStatement deleteAppointments = connect.prepareStatement(
                        "DELETE FROM appointments WHERE Customer_ID = ?"
                );
                deleteAppointments.setInt(1, customer.getId());
                deleteAppointments.executeUpdate();

            } catch (SQLException e) {
                // Display an error message and return
                e.printStackTrace();
                return bool;
            }
        }

        // Delete the customer
        try {
            PreparedStatement deleteCustomer = connect.prepareStatement(
                    "DELETE FROM customers WHERE Customer_ID = ?"
            );
            deleteCustomer.setInt(1, customer.getId());
            int rowsAffected = deleteCustomer.executeUpdate();
            bool = rowsAffected > 0;
            return bool;

        } catch (SQLException e) {
            // Display an error message and return
            e.printStackTrace();

            return bool;
        }

    }

    public ArrayList<Integer> getCustomerIds() {
        ArrayList<Integer> customerIds = new ArrayList<>();
        try {
            DatabaseConnection dBC = new DatabaseConnection();
            Connection connect = dBC.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Customer_ID FROM customers");
            while (rs.next()) {
                customerIds.add(rs.getInt("Customer_ID"));
            }
            close3Connections(stmt, connect, rs);
        } catch (SQLException e) {

        }
        return customerIds;
    }
}

