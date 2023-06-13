package com.Scheduling.Web.App.controllers;

import com.Scheduling.Web.App.models.Appointment;
import com.Scheduling.Web.App.models.Customer;
import com.Scheduling.Web.App.repository.JDBCContactRepository;
import com.Scheduling.Web.App.repository.JDBCCustomerRepository;
import com.Scheduling.Web.App.services.AppointmentService;
import com.Scheduling.Web.App.services.CustomerService;
import com.Scheduling.Web.App.services.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AppointmentsViewController {
    @GetMapping("/appointmentsView")
    public String displayAppointments(Authentication authentication, Model model) {
        return "appointmentsView";
    }

    @PostMapping("/api/saveEditAppointment")
    public @ResponseBody String saveAsEdit(@RequestBody Appointment appointment, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Saving as an edit");
        AppointmentService appointService = new AppointmentService();
        // Process the Customer object and save it as an edit
        boolean result = appointService.updateAppointment(appointment,username);

        // Return a success message or error message
        if (result) {
            return "Success";
        } else {
            return "Error";
        }
    }
    @PostMapping("/api/saveNewAppointment")
    public @ResponseBody String saveAsNew(@RequestBody Appointment appointment, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Saving as a new appointment");
        AppointmentService appointService = new AppointmentService();
        // Process the Customer object and save it as an edit
        boolean result = appointService.newAppointment(appointment,username);

        // Return a success message or error message
        if (result) {
            return "Success";
        } else {
            return "Error";
        }
    }
    @PostMapping("/api/deleteAppointment")
    public @ResponseBody String deleteAppointment(@RequestBody Appointment appointment, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Deleting appointment");
        AppointmentService appointService = new AppointmentService();
        // Process the Customer object and save it as an edit
        boolean result = appointService.deleteAppointment(appointment);

        // Return a success message or error message
        if (result) {
            return "Success";
        } else {
            return "Error";
        }
    }
    @GetMapping("/api/getAllAppointments")
    public @ResponseBody List<Appointment> getAppointments(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Getting appointments");
        AppointmentService appointService = new AppointmentService();
        // Process the Customer object and save it as an edit
        List<Appointment> result = appointService.getAllAppointments();

        // Return a success message or error message
        return result;
    }
    @GetMapping("/api/getThisWeeksAppointments")
    public @ResponseBody List<Appointment> getThisWeeksAppointments( Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Getting appointments");
        AppointmentService appointService = new AppointmentService();
        // Process the Customer object and save it as an edit
        List<Appointment> result = appointService.getAllAppointmentsByWeek();

        // Return a success message or error message
        return result;
    }
    @GetMapping("/api/getThisMonthsAppointments")
    public @ResponseBody List<Appointment> getThisMonthsAppointments( Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Getting appointments By Month");
        AppointmentService appointService = new AppointmentService();
        // Process the Customer object and save it as an edit
        List<Appointment> result = appointService.getAllAppointmentsByMonth();

        // Return a success message or error message
        return result;
    }
    @GetMapping("/api/getAllCustomerIDs")
    public @ResponseBody ArrayList<Integer> getAllCustomerIDs() {
        System.out.println("Getting all customer IDs");
        CustomerService customerService = new CustomerService();
        // Process the Customer object and save it as an edit
        ArrayList<Integer> result = customerService.getCustomerIds();

        // Return a success message or error message
        return result;
    }
    @GetMapping("/api/getAllContacts")
    public @ResponseBody ArrayList<String> getAllContacts() {
        System.out.println("Getting all contacts");

        // Process the Customer object and save it as an edit
        ArrayList<String> result = JDBCContactRepository.getContactNames();

        // Return a success message or error message
        return result;
    }
    @GetMapping("/api/getAllUserIDs")
    public @ResponseBody ArrayList<Integer> getAllUserIDs() {
        System.out.println("Getting all user IDs");

        // gets the user IDs from the database through the service
        ArrayList<Integer> result = UserDetailsServiceImpl.getAllUserIds();

        // Return a success message or error message
        return result;
    }
    @GetMapping("/api/getAllTypes")
    public @ResponseBody ArrayList<String> getAllTypes() {
        System.out.println("Getting all types");

        // gets the user IDs from the database through the service
        ArrayList<String> result = AppointmentService.getAllTypes();

        // Return a success message or error message
        return result;
    }
}
