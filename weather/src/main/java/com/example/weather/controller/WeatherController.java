package com.example.weather.controller;

import com.example.weather.model.weatherdata;
import com.example.weather.repository.WeatherDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WeatherController {

    @Autowired
    private WeatherDataService weatherService;

    @GetMapping("/result")
    public String getResult(Model model) {
        List<weatherdata> dataList = weatherService.getAllData();
        model.addAttribute("dataList", dataList);
        return "page";
    }
}
