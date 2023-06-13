package com.Scheduling.Web.App.services;

import com.Scheduling.Web.App.models.Appointment;
import com.Scheduling.Web.App.models.MonthlyTotal;
import com.Scheduling.Web.App.models.TypeCount;
import com.Scheduling.Web.App.repository.JDBCAppointmentRepository;
import com.Scheduling.Web.App.repository.JDBCContactRepository;

import java.time.LocalDate;
import java.util.ArrayList;

public class AppointmentService {
    public boolean updateAppointment(Appointment appointment, String username){
        boolean bool =false;
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        LocalDate date = HelperServiceClass.getcurrentCentralTime();
        appointment.setEventDateTime(date.toString());
        System.out.println(appointment.toString());
        if(jdar.editAppointment(appointment,username)){
            bool= true;
        }
        return bool;
    }
    public boolean newAppointment(Appointment appointment, String username){
        boolean bool =false;
        JDBCContactRepository jcr = new JDBCContactRepository();
        LocalDate date = HelperServiceClass.getcurrentCentralTime();
        appointment.setEventDateTime(date.toString());
        appointment.setContact_ID(jcr.getContactIDfromName(appointment.getContact()));
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        if(jdar.createNewAppointment(appointment,username)){
            bool= true;
        }
        return bool;
    }
    public boolean deleteAppointment(Appointment appointment){
        boolean bool =false;
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        if(jdar.deleteAppointment(appointment)){
            bool= true;
        }
        return bool;
    }
    public Appointment getAppointment(int appointmentID){
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        return jdar.getAppointment(appointmentID);
    }
    public ArrayList<Appointment> getAllAppointments(){
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        return jdar.getAllAppointments();
    }
    public ArrayList<Appointment> getAllAppointmentsByMonth() {
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        LocalDate date = HelperServiceClass.getcurrentCentralTime();
        return jdar.getAppointmentsforThisMonth(date);
    }
    public ArrayList<Appointment> getAllAppointmentsByWeek(){
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        LocalDate date = HelperServiceClass.getcurrentCentralTime();
        return jdar.filterByWeek(date);
    }
    public ArrayList<Appointment> getAllAppointmentsByType(String type){
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        return jdar.getAppointmentsByType(type);
    }
    public static ArrayList<String> getAllTypes(){
        ArrayList<String> types = new ArrayList<String>();
        types.add("In-Office");
        types.add("Phone");
        types.add("Video");
        types.add("Home Visit");
        types.add("Initial Consultation");
        return types;
    }
    public ArrayList<Appointment> getAllAppointmentsByContact(String contact){
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        int contactID = new JDBCContactRepository().getContactIDfromName(contact);
        System.out.println(contactID);
        return jdar.getAppointmentsByContactID(contactID);
    }
    public ArrayList<MonthlyTotal> getMonthlyTotals(){
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        return jdar.getTotalsByMonth();
    }
    public ArrayList<TypeCount> getTotalsByType(){
        JDBCAppointmentRepository jdar = new JDBCAppointmentRepository();
        return jdar.getTotalsByType();
    }
}
