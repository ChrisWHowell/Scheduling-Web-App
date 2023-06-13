package com.Scheduling.Web.App.controllers;

import com.Scheduling.Web.App.models.Appointment;
import com.Scheduling.Web.App.models.LoginStats;
import com.Scheduling.Web.App.models.MonthlyTotal;
import com.Scheduling.Web.App.models.TypeCount;
import com.Scheduling.Web.App.repository.JDBCContactRepository;
import com.Scheduling.Web.App.services.AppointmentService;
import com.Scheduling.Web.App.services.HelperServiceClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class ReportsController {
    @GetMapping("/reportsView")
    public String displayAppointments(Authentication authentication, Model model) {
        return "reportsView";
    }

    @GetMapping("/api/getAllContactsforReport")
    public @ResponseBody ArrayList<String> getAllContacts() {

        ArrayList<String> result = JDBCContactRepository.getContactNames();

        return result;
    }

    @GetMapping("/api/getTotalsByMonth")
    public @ResponseBody ArrayList<MonthlyTotal> getTotalsByMonth() {

        AppointmentService appointService = new AppointmentService();
        ArrayList<MonthlyTotal> result = appointService.getMonthlyTotals();

        // Return a success message or error message
        return result;
    }

    @GetMapping("/api/getTotalsByType")
    public @ResponseBody ArrayList<TypeCount> getTotalsByType() {

        AppointmentService appointService = new AppointmentService();

        ArrayList<TypeCount> result = appointService.getTotalsByType();


        return result;
    }

    @PostMapping("/api/getContactSchedule")
    public @ResponseBody ArrayList<Appointment> getContactSchedule(@RequestBody String contactName, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppointmentService appointService = new AppointmentService();

        ArrayList<Appointment> result = appointService.getAllAppointmentsByContact(contactName);


        return result;
    }


    @PostMapping("/api/getLoginStats")
    public @ResponseBody ArrayList<LoginStats> getLoginStats(@RequestBody Map<String, String> dateRange) {
        System.out.println("Getting Login Stats");
        String start = dateRange.get("startDate");
        String end = dateRange.get("endDate");

        LocalDate startDate = start != null && !start.isEmpty() ? LocalDate.parse(start) : null;
        LocalDate endDate = end != null && !end.isEmpty() ? LocalDate.parse(end) : null;
    System.out.println("Start Date: " + startDate+ " End Date: " + endDate);
        HelperServiceClass helperService = new HelperServiceClass();
        ArrayList<LoginStats> loginStats = helperService.getLoginStats(startDate, endDate);

        return loginStats;
    }
}
