package com.moodyjun.covid19trackingapplication.api;

import com.moodyjun.covid19trackingapplication.model.LocationStatus;
import com.moodyjun.covid19trackingapplication.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStatus> confirmedCovidData = coronaVirusDataService.getConfirmedCovidData();
        int totalCases = confirmedCovidData.stream().mapToInt(LocationStatus::getTotalCases).sum();
        model.addAttribute("locationStats", confirmedCovidData);
        model.addAttribute("totalCases", totalCases);
        return "home";
    }
}