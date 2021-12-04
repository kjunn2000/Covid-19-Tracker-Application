package com.moodyjun.covid19trackingapplication.api;

import com.moodyjun.covid19trackingapplication.model.OverallLocationStatus;
import com.moodyjun.covid19trackingapplication.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class WeeklyAndMonthlyController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/weekly")
    public String weekly(Model model) {
        List<OverallLocationStatus> summaryData= coronaVirusDataService.getSummaryData();
        model.addAttribute("summaryData", summaryData);
        model.addAttribute("type", "weekly");
        return "weeklyAndMonthly";
    }

    @GetMapping("/monthly")
    public String monthly(Model model) {
        List<OverallLocationStatus> summaryData= coronaVirusDataService.getSummaryData();
        model.addAttribute("summaryData", summaryData);
        model.addAttribute("type", "monthly");
        return "weeklyAndMonthly";
    }

    @GetMapping("/weekly/search")
    public String weeklySearch(Model model, @RequestParam(value = "country") String country) {
        model.addAttribute("summaryData", coronaVirusDataService.getSummaryDataByCountry(country));
        model.addAttribute("type", "weekly");
        return "weeklyAndMonthly";
    }

    @GetMapping("/monthly/search")
    public String monthlySearch(Model model, @RequestParam(value = "country") String country) {
        model.addAttribute("summaryData", coronaVirusDataService.getSummaryDataByCountry(country));
        model.addAttribute("type", "monthly");
        return "weeklyAndMonthly";
    }
}