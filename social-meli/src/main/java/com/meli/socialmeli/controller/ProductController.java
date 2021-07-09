package com.meli.socialmeli.controller;


import com.meli.socialmeli.dto.CountPromoSellerDTO;
import com.meli.socialmeli.dto.ListPromoProdSellerDTO;
import com.meli.socialmeli.service.SellerService;
import com.meli.socialmeli.dto.UserFollowingPostsDTO;
import com.meli.socialmeli.entity.Post;
import com.meli.socialmeli.service.ClientService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final SellerService sellerService;

    private final ClientService clientService;

    @Autowired
    public ProductController(SellerService sellerService, ClientService clientService) {
        this.sellerService = sellerService;
        this.clientService = clientService;
    }

    @ApiOperation(value = "Funcionalidade para buscar a quantidade de produtos promocionais de um vendedor")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userId", value = "Id do vendedor que se deseja realizar a busca", example = "6"),
    })
    @GetMapping("{userId}/countPromo")
    @ResponseStatus(HttpStatus.OK)
    public CountPromoSellerDTO getCountPromoSeller(@PathVariable int userId) {
        return sellerService.getCountPostPromoSeller(userId);
    }

    @ApiOperation(value = "Funcionalidade para buscar a lista de produtos promocionais de um vendedor")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userId", value = "Id do vendedor que se deseja realizar a busca", example = "6"),
    })
    @GetMapping("{userId}/list")
    @ResponseStatus(HttpStatus.OK)
    public ListPromoProdSellerDTO getListPromoProdSeller(@PathVariable int userId, @RequestParam(required = false) String order) {
        return sellerService.getListPromoProdSeller(userId, order);
    }

    @ApiOperation(value = "Funcionalidade para buscar a lista de publicações das últimas duas semanas feitas pelos vendedores que um cliente segue")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userId", value = "Id do cliente que se deseja realizar a busca", example = "1"),
    })
    @GetMapping("followed/{userId}/list")
    @ResponseStatus(HttpStatus.OK)
    public UserFollowingPostsDTO listSellersPostsByUserId(@PathVariable int userId, @RequestParam(required = false) String order) {
        List<Post> posts = clientService.getUserFollowingSellersPosts(userId, order);

        return new UserFollowingPostsDTO(userId, posts);
    }
}
