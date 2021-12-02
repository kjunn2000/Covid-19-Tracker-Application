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
public class HighestAndLowestController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/highest-and-lowest")
    public String home(Model model) {
        List<OverallLocationStatus> summaryData= coronaVirusDataService.getSummaryData();

        model.addAttribute("summaryData", summaryData);
        return "highestAndLowest";
    }

    @GetMapping("/highest-and-lowest/search")
    public String homeSearch(Model model, @RequestParam(value = "country") String country) {
        List<OverallLocationStatus> summaryData= coronaVirusDataService.getSummaryData();
        List<OverallLocationStatus> filteredData = summaryData.stream().filter(overallLocationStatus -> overallLocationStatus.getLocation().getCountry().contains(country)).collect(Collectors.toList());

        model.addAttribute("summaryData", filteredData);
        return "highestAndLowest";
    }
}