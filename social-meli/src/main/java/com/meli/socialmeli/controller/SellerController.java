package com.meli.socialmeli.controller;

import com.meli.socialmeli.dto.FollowersCountDTO;
import com.meli.socialmeli.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.meli.socialmeli.dto.FollowersListDTO;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class SellerController {

    private final SellerService sellerService;

    @Autowired
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }


    @GetMapping("/users/{userId}/followers/count")
    @ResponseStatus(value = HttpStatus.OK)
    public FollowersCountDTO getFollowersSellerCount(@PathVariable int userId) {
        return sellerService.getFollowersSellerCount(userId);
    }

    @GetMapping("/users/{userId}/followers/list")
    public FollowersListDTO followersList(@PathVariable int userId,  @RequestParam(required = false) String order){
        return sellerService.getFollowers(userId,order);
    }

}
