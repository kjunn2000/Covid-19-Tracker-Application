package com.moodyjun.covid19trackingapplication.services;

import com.moodyjun.covid19trackingapplication.model.Location;
import com.moodyjun.covid19trackingapplication.model.LocationStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrologService {

    static final String PL_FILE = "covid_confirmed_data.pl";

    public void writeToPrologFile(List<LocationStatus> locationStatusList){
        List<String> knowledgeData = locationStatusList.stream()
                .map(locationStatus -> {
                    Location location = locationStatus.getLocation();
                    String country = location.getCountry()
                            .toLowerCase()
                            .replaceAll(" |'","_")
                            .replaceAll(",","");
                    String state = location.getState()
                            .trim()
                            .toLowerCase()
                            .replaceAll(" ","_")
                            .replaceAll(",","");
                    country = country.endsWith("*") ? country.substring(0,country.length()-1) : country;
                    return "totalCases(" + country
                            + (state.isEmpty() ? "" : "(" + state + ")" )
                            + "," + locationStatus.getTotalCases() + ").\n";
                }).collect(Collectors.toList());
        try {
            Files.writeString(Paths.get(PL_FILE), String.join("",knowledgeData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
