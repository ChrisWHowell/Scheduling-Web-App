package com.Scheduling.Web.App.repository;

import com.Scheduling.Web.App.models.Appointment;
import com.Scheduling.Web.App.models.MonthlyTotal;
import com.Scheduling.Web.App.models.TypeCount;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class JDBCAppointmentRepository {

    /**
     * This method will return a boolean value based on if the appointment was successfully changed
     * @return boolean
     * @param appointment
     * @param username
     */
    public boolean editAppointment(Appointment appointment, String username) {
        boolean bool = false;

        try {

            int contact_ID = JDBCContactRepository.getContactIDfromName(appointment.getContact());
            DatabaseConnection dBC = new DatabaseConnection();
            Connection connect = dBC.getConnection();
            PreparedStatement stmt = connect.prepareStatement("UPDATE appointments SET Title = ?, Description  = ?, Location = ?, Type = ?, " +
                    "Start = ?," + " End = ?, Last_Update = ?, Last_Updated_By= ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?");
            stmt.setString(1, appointment.getTitle());
            stmt.setString(2, appointment.getDescription());
            stmt.setString(3, appointment.getLocation());
            stmt.setString(4, appointment.getType());
            stmt.setString(5, appointment.getStart_DateTime());
            stmt.setString(6, appointment.getEnd_DateTime());
            stmt.setString(7, appointment.getEventDateTime());
            stmt.setString(8, username);
            stmt.setInt(9, appointment.getCustomer_ID());
            stmt.setInt(10, appointment.getUser_ID());
            stmt.setInt(11, contact_ID);
            stmt.setInt(12, appointment.getId());
            int rowsAffected = stmt.executeUpdate();
            bool = rowsAffected > 0;

            stmt.close();
            connect.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * This method will return a boolean value based on if the appointment was successfully created
     * @return boolean
     * @param appointment
     * @param username
     */
    public boolean createNewAppointment(Appointment appointment, String username) {
        boolean bool = false;
        try {
            DatabaseConnection dbc = new DatabaseConnection();
            Connection conn = dbc.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO appointments (Title,Description,Location,Type,Start,End,Create_Date,Created_By,Last_Update,"
                    + "Last_Updated_By,Customer_ID,User_ID,Contact_ID) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmt.setString(1, appointment.getTitle());
            stmt.setString(2, appointment.getDescription());
            stmt.setString(3, appointment.getLocation());
            stmt.setString(4, appointment.getType());
            stmt.setString(5, appointment.getStart_DateTime());
            stmt.setString(6, appointment.getEnd_DateTime());
            stmt.setString(7, appointment.getEventDateTime());
            stmt.setString(8, username);
            stmt.setString(9, appointment.getEventDateTime());
            stmt.setString(10, username);
            stmt.setInt(11, appointment.getCustomer_ID());
            stmt.setInt(12, appointment.getUser_ID());
            stmt.setInt(13, appointment.getContact_ID());
            int rowsAffected = stmt.executeUpdate();
            bool = rowsAffected > 0;
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * This method returns a boolean indicating if the appointment was successfully deleted
     * @param appointment
     * @return
     */
    public boolean deleteAppointment(Appointment appointment) {
        boolean bool = false;

        try {
            DatabaseConnection dBC = new DatabaseConnection();
            Connection connect = dBC.getConnection();
            PreparedStatement deleteAppointments = connect.prepareStatement(
                    "DELETE FROM appointments WHERE Appointment_ID = ?");
            deleteAppointments.setInt(1, appointment.getId());
            int rowsAffected = deleteAppointments.executeUpdate();
            bool = rowsAffected > 0;

            deleteAppointments.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * This method checks for overlapping appointments in the database and returns true if there is an overlap
     * @param appointment
     * @return
     */
    public boolean checkforOverlappingAppointments(Appointment appointment) {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        String startDateTime = appointment.getStart_DateTime();
        String endDateTime = appointment.getEnd_DateTime();
        // Check for overlapping appointments in the database
        try {
            String query = "Select * from appointments where (Start between ? and ?) or (End between ? and ?)";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, startDateTime);
            statement.setString(2, endDateTime);
            statement.setString(3, startDateTime);
            statement.setString(4, endDateTime);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method returns an ArrayList of all appointments in the database for the next week
     * @return
     */
    public ArrayList<Appointment> filterByWeek(LocalDate date) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            String query = "Select * from appointments where Start between ? and ?";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, date.toString());
            statement.setString(2, date.plusDays(7).toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setLocation(rs.getString("Location"));
                appointment.setType(rs.getString("Type"));
                appointment.setStart_DateTime(rs.getString("Start"));
                appointment.setEnd_DateTime(rs.getString("End"));
                appointment.setEventDateTime(rs.getString("Create_Date"));
                appointment.setCustomer_ID(rs.getInt("Customer_ID"));
                appointment.setUser_ID(rs.getInt("User_ID"));
                appointment.setContact_ID(rs.getInt("Contact_ID"));
                appointment.setContact(JDBCContactRepository.getContactNameFromID(appointment.getContact_ID()));
                appointments.add(appointment);
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * This method returns an ArrayList of all appointments in the database for the next month
     * @return ArrayList<Appointment>
     * @param todayDateTime
     */
    public ArrayList<Appointment> getAppointmentsforThisMonth(LocalDate todayDateTime) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            String query = "Select * from appointments where Start between ? and ?";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, todayDateTime.toString());
            statement.setString(2, todayDateTime.plusMonths(1).toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setLocation(rs.getString("Location"));
                appointment.setType(rs.getString("Type"));
                appointment.setStart_DateTime(rs.getString("Start"));
                appointment.setEnd_DateTime(rs.getString("End"));
                appointment.setEventDateTime(rs.getString("Create_Date"));
                appointment.setCustomer_ID(rs.getInt("Customer_ID"));
                appointment.setUser_ID(rs.getInt("User_ID"));
                appointment.setContact_ID(rs.getInt("Contact_ID"));
                appointment.setContact(JDBCContactRepository.getContactNameFromID(appointment.getContact_ID()));
                appointments.add(appointment);
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * This method returns an ArrayList of all appointments in the database
     * @return ArrayList<Appointment>
     */
    public ArrayList<Appointment> getAllAppointments(){
        ArrayList<Appointment> appointments = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            String query = "Select * from appointments";
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setLocation(rs.getString("Location"));
                appointment.setType(rs.getString("Type"));
                appointment.setStart_DateTime(rs.getString("Start"));
                appointment.setEnd_DateTime(rs.getString("End"));
                appointment.setEventDateTime(rs.getString("Create_Date"));
                appointment.setCustomer_ID(rs.getInt("Customer_ID"));
                appointment.setUser_ID(rs.getInt("User_ID"));
                appointment.setContact_ID(rs.getInt("Contact_ID"));
                appointment.setContact(JDBCContactRepository.getContactNameFromID(appointment.getContact_ID()));
                appointments.add(appointment);
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Returns the appointment details for the appointment ID passed in
     * @param appointmentID
     * @return Appointment
     */
    public Appointment getAppointment(int appointmentID) {
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            String query = "Select * from appointments where Appointment_ID = ?";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1, appointmentID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setLocation(rs.getString("Location"));
                appointment.setType(rs.getString("Type"));
                appointment.setStart_DateTime(rs.getString("Start"));
                appointment.setEnd_DateTime(rs.getString("End"));
                appointment.setEventDateTime(rs.getString("Create_Date"));
                appointment.setCustomer_ID(rs.getInt("Customer_ID"));
                appointment.setUser_ID(rs.getInt("User_ID"));
                appointment.setContact_ID(rs.getInt("Contact_ID"));
                return appointment;
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method returns an ArrayList of all appointments in the database for a specific type
     * @param type
     * @return ArrayList<Appointment>
     */
    public ArrayList<Appointment> getAppointmentsByType(String type) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            String query = "Select * from appointments where Type = ?";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, type);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setLocation(rs.getString("Location"));
                appointment.setType(rs.getString("Type"));
                appointment.setStart_DateTime(rs.getString("Start"));
                appointment.setEnd_DateTime(rs.getString("End"));
                appointment.setEventDateTime(rs.getString("Create_Date"));
                appointment.setCustomer_ID(rs.getInt("Customer_ID"));
                appointment.setUser_ID(rs.getInt("User_ID"));
                appointment.setContact_ID(rs.getInt("Contact_ID"));
                appointments.add(appointment);
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Returns an ArrayList of all appointments in the database that contains a specific contact ID
     * @param contactID
     * @return
     */
    public ArrayList<Appointment> getAppointmentsByContactID(int contactID) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        try {
            String query = "Select * from appointments where Contact_ID = ?";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1, contactID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setLocation(rs.getString("Location"));
                appointment.setType(rs.getString("Type"));
                appointment.setStart_DateTime(rs.getString("Start"));
                appointment.setEnd_DateTime(rs.getString("End"));
                appointment.setEventDateTime(rs.getString("Create_Date"));
                appointment.setCustomer_ID(rs.getInt("Customer_ID"));
                appointment.setUser_ID(rs.getInt("User_ID"));
                appointment.setContact_ID(rs.getInt("Contact_ID"));
                appointments.add(appointment);
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Returns an ArrayList of all MonthlyTotal which contains the total number of appointments for each month
     * @return ArrayList<MonthlyTotal>
     */
    public ArrayList<MonthlyTotal> getTotalsByMonth() {
        ArrayList<MonthlyTotal> months = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        months.add(new MonthlyTotal("January", 0));
        months.add(new MonthlyTotal("February", 0));
        months.add(new MonthlyTotal("March", 0));
        months.add(new MonthlyTotal("April", 0));
        months.add(new MonthlyTotal("May", 0));
        months.add(new MonthlyTotal("June", 0));
        months.add(new MonthlyTotal("July", 0));
        months.add(new MonthlyTotal("August", 0));
        months.add(new MonthlyTotal("September", 0));
        months.add(new MonthlyTotal("October", 0));
        months.add(new MonthlyTotal("November", 0));
        months.add(new MonthlyTotal("December", 0));
        try {
            String query = "SELECT MONTH(Start) AS month, COUNT(*) AS count FROM appointments GROUP BY MONTH(Start)";
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                MonthlyTotal monthlyTotal = months.get(month - 1);
                monthlyTotal.setAppointmentCount(count);
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return months;
    }

    /**
     * Returns an ArrayList of all TypeCount which contains the total number of appointments for each type
     * @return ArrayList<TypeCount>
     */
    public ArrayList<TypeCount> getTotalsByType() {
        ArrayList<TypeCount> types = new ArrayList<>();
        DatabaseConnection dBC = new DatabaseConnection();
        Connection connect = dBC.getConnection();
        types.add(new TypeCount("In-Office", 0));
        types.add(new TypeCount("Phone", 0));
        types.add(new TypeCount("Video", 0));
        types.add(new TypeCount("Initial Consultation", 0));

        try {
            String query = "SELECT Type, COUNT(*) AS count FROM appointments GROUP BY Type";
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String type = rs.getString("Type");
                int count = rs.getInt("count");
                System.out.println("The type is: " + type + " and the count is: " +count);
                for(TypeCount tc: types){
                    if(tc.getTypeName().equals(type)){
                        tc.setAppointmentCount(count);
                    }
                }
            }
            rs.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }
}