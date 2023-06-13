package com.Scheduling.Web.App.controllers;


import com.Scheduling.Web.App.services.UserDetailsServiceImpl;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;


import java.util.Map;

/*The Controller class main purpose is to handle the get and post requests of the web application
 */
@Controller
public class WebController {
    private UserDetailsServiceImpl userService;
    @GetMapping("/login")
    public String login() {

        return "login";
    }


}
