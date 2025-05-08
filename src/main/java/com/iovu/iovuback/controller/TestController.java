package com.iovu.iovuback.controller;

import com.iovu.iovuback.domain.Restaurant;
import com.iovu.iovuback.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    private TestService restaurantService;

    public TestController(TestService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/restaurants")
    public List<Restaurant> retrieveAllRestaurant() {
        return restaurantService.selectAll();
    }
}
