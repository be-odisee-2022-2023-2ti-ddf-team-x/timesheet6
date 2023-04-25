package be.odisee.ti2.ddf.timesheet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login-error")
    public String loginerror(Model model) {
        model.addAttribute("error", true);
        return "login";
    }
}
