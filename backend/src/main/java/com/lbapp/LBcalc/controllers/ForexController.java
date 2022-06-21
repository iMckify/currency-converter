package com.lbapp.LBcalc.controllers;

import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.services.ForexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/Forex")
public class ForexController {
    public static final Logger logger = LoggerFactory.getLogger(ForexController.class);

    @Autowired
    private ForexService forexService;

    @GetMapping("/current")
    public List<CurrentFxRate> getLastPrices(){
        return this.forexService.getAllCurrent();
    }
}
