package com.Scheduling.Web.App.repository;

import com.Scheduling.Web.App.models.Role;
import com.Scheduling.Web.App.models.UserEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCUserRepository {

    public static UserEntity getUserEntityByName(String userName){
        UserEntity user = null;
        Connection connect = null;
        try {
            connect = DatabaseConnection.getConnection();
            String query = "Select * From users WHERE User_Name = ?;";
            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, userName);
            ResultSet rs1 = stmt.executeQuery();
            while (rs1.next()) {
                user = new UserEntity();
                user.setId(rs1.getInt("User_ID"));
                user.setUsername(rs1.getString("User_Name"));
                user.setPassword(rs1.getString("Password"));
            }
            rs1.close();
            stmt.close();
            if(user == null){
                connect.close();
                return null;
            }
            String query2 = "Select roles.Role_ID , roles.Role_Name "
                    +"FROM users "
                    +"JOIN user_role  ON users.User_ID = user_role.User_ID "
                    +"JOIN roles  ON user_role.Role_ID = roles.Role_ID "
                    +"WHERE users.User_ID = ? ;";

            PreparedStatement stmt2 = connect.prepareStatement(query2);
            stmt2.setInt(1, user.getId());
            ResultSet rs2 = stmt2.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (rs2.next()) {
                Role role = new Role();
                role.setId(rs2.getInt("Role_ID"));
                role.setName(rs2.getString("Role_Name"));
                roles.add(role);
            }
            user.setRoles(roles);
            rs2.close();
            stmt2.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("There was a problem in getting the UserEntity from the DB");
        } finally {
            // close the DB connection
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("There was a problem in closing the DB in method getUserEntityByName()");
                }
            }
        }

        return user;
    }
    public static ArrayList<Integer> getAllUserIds(){
        ArrayList<Integer> userIds = new ArrayList<>();
        try{
            Connection connect = DatabaseConnection.getConnection();
            String query = "SELECT User_ID FROM users;";
            PreparedStatement stmt = connect.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                userIds.add(rs.getInt("User_ID"));
            }
            rs.close();
            stmt.close();
            connect.close();

        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("There was a problem in getting the UserEntity from the DB");
        }
        return userIds;
    }
}
