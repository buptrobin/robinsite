package com.robinwang.site.controller;

import com.robinwang.site.service.StatisticCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by robin on 2017/7/6.
 */
@RestController
@EnableAutoConfiguration
public class CSVController {

    protected static Logger LOG = LoggerFactory.getLogger(CSVController.class);

    @Autowired
    private StatisticCSV statisticCSV;
    @RequestMapping("/")
    public String home() {
        return "Hello World";
    }

    @RequestMapping("/csv/go")
    public String csvv() throws Exception {
        statisticCSV.readCSV();
        return "This is CSV home";
    }

    @RequestMapping(value = "/test.htm")
    public String Hello(ModelMap map) {
        map.addAttribute("message", "yes");

        return "test";
    }
}
