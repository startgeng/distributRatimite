package com.kevin.ratemlimiter.controller;

import com.kevin.ratemlimiter.annotion.AccesstionLimit;
import com.kevin.ratemlimiter.util.AccessLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kevin
 * @version 1.0
 * @date 2021-07-02 21:38
 */
@RestController
@Slf4j
public class RatelimiterController {

    @Autowired
    private AccessLimiter accessLimiter;

    @GetMapping(value = "/test")
    public String test(){
        accessLimiter.limitAccess("ratelimit-test",1);
        return "success";
    }

    @AccesstionLimit(limit = 1)
    public String testAnnotion(){
        return "success";
    }
}
