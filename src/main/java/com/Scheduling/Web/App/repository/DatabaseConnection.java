package com.Scheduling.Web.App.repository;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 * @author Chris Howell
 * @version 1.0
 * Represents an DatabaseConnection Object in the application. Its primary use is for creating a connection
 * to the database, to be used for various inputs and outputs.
 */
public class DatabaseConnection {



    /**
     * This method creates the connection to the database and returns it
     * @return Connection
     */
    protected static Connection getConnection(){

        String dbName = "client_schedule";
        String dbUser = "root";
        String dbPassword = "MSLMo_Disk_21";
        String url = "jdbc:mysql://localhost:3306/" + dbName;
        Connection dblink = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            dblink = DriverManager.getConnection(url,dbUser,dbPassword);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("There be problems a brewing");
        }
        return dblink;
    }

}
