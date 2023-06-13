package com.Scheduling.Web.App.services;
import com.Scheduling.Web.App.models.UserEntity;
import com.Scheduling.Web.App.repository.JDBCUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *The UserDetailsServiceImpl class implements the UserDetailsService interface.
 * This class
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = JDBCUserRepository.getUserEntityByName(username);
        boolean bool = true;
        if (userEntity == null) {
            logLoginAttempt(username, false);
            bool = false;
            System.out.println(" User not found with username in loaduserbyname: ");
            throw new UsernameNotFoundException("User not found with username: " + username);
        }


        // Make sure that the necessary properties are not null or empty before creating the User object
        String password = userEntity.getPassword();
        if (password == null || password.isEmpty()) {
            System.out.println(" User password cannot be null or empty:");
            throw new IllegalArgumentException("User password cannot be null or empty");
        }

        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        return new User(userEntity.getUsername(), userEntity.getPassword(), authorities);
    }
    public static ArrayList<Integer> getAllUserIds() {
        return JDBCUserRepository.getAllUserIds();
    }
    public static void logLoginAttempt(String username, boolean success) {
        try {
            // Get the current time in the Central Time Zone
            ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("America/Chicago"));

            // Format the current time to match the database format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Create or open the login_activity.txt file
            FileWriter fileWriter = new FileWriter("login_activity.txt", true);

            // Write the login attempt information to the file
            fileWriter.write(String.format("%s - %s - %s\n", username, currentTime.format(formatter), success ? "Success" : "Failed"));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
