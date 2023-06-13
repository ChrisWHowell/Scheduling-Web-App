package com.Scheduling.Web.App.repository;

import java.sql.*;
import java.util.ArrayList;

public class JDBCContactRepository {

    public static int getContactIDfromName(String contactName){
        contactName = contactName.replace("\"", "");

        int contactID = 1;
        try{

            DatabaseConnection dbc = new DatabaseConnection();
            Connection conn = dbc.getConnection();
            PreparedStatement stmt = conn.prepareStatement("Select Contact_ID FROM " +
                    "contacts Where Contact_Name = ?");
            stmt.setString(1,contactName);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                contactID = rs.getInt("Contact_ID");

            }

            rs.close();
            stmt.close();
            conn.close();


        }catch(SQLException e){
            e.printStackTrace();
        }
        return contactID;
    }
    public static ArrayList<String> getContactNames(){
        ArrayList<String> contactNames = new ArrayList<>();
        try{

            DatabaseConnection dbc = new DatabaseConnection();
            Connection conn = dbc.getConnection();
            PreparedStatement stmt = conn.prepareStatement("Select Contact_Name FROM " +
                    "contacts");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                contactNames.add(rs.getString("Contact_Name"));

            }
            conn.close();
            rs.close();
            stmt.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return contactNames;
    }
    public static String getContactNameFromID(int contactID){
        String contactName = "";
        try{

            DatabaseConnection dbc = new DatabaseConnection();
            Connection conn = dbc.getConnection();
            PreparedStatement stmt = conn.prepareStatement("Select Contact_Name FROM " +
                    "contacts Where Contact_ID = ?");
            stmt.setInt(1,contactID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                contactName = rs.getString("Contact_Name");
            }
            conn.close();
            rs.close();
            stmt.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return contactName;
    }
}
