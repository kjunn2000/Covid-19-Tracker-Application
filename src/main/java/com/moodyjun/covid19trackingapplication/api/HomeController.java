package com.moodyjun.covid19trackingapplication.api;

import com.moodyjun.covid19trackingapplication.model.OverallLocationStatus;
import com.moodyjun.covid19trackingapplication.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<OverallLocationStatus> summaryData= coronaVirusDataService.getSummaryData();

        model.addAttribute("summaryData", summaryData);
        return "home";
    }

    @GetMapping("/search")
    public String homeSearch(Model model, @RequestParam(value = "country") String country) {
        model.addAttribute("summaryData", coronaVirusDataService.getSummaryDataByCountry(country));
        return "home";
    }
}