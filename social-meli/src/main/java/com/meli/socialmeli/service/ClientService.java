package com.meli.socialmeli.service;

import com.meli.socialmeli.entity.Client;
import com.meli.socialmeli.entity.Post;
import com.meli.socialmeli.entity.Seller;
import com.meli.socialmeli.repository.ClientRepository;
import com.meli.socialmeli.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClientService {

    private final SellerRepository sellerRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, SellerRepository sellerRepository) {
        this.clientRepository = clientRepository;
        this.sellerRepository = sellerRepository;
    }

    public Client getUserFollowingSellers(int userId, String order) {

        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Não foi encontrado nenhum usuário cliente com o id: " + userId));

        client.getFollowing().sort((c1, c2) -> new StringComparator(order).compare(c1.getUsername(),c2.getUsername()));
        return client;
    }

    public List<Post> getUserFollowingSellersPosts(int userId, String order) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Não foi encontrado nenhum usuário cliente com o id: " + userId));

        List<Seller> following = client.getFollowing();
        List<Post> postList = new ArrayList<>();

        following
                .stream()
                .map(Seller::getPosts)
                .forEach(posts -> posts
                        .forEach(p -> {
                                    if (p.getDate().isAfter(LocalDate.now().minusWeeks(2)))
                                        postList.add(p);
                                }
                        )
                );

        postList.sort((o1, o2) -> {
            if (o1.getDate() == null || o2.getDate() == null) {
                return 0;
            }

            if ("date_asc".equals(order)) {
                return o1.getDate().compareTo(o2.getDate());
            }

            return o2.getDate().compareTo(o1.getDate());
        });

        return postList;
    }

    public void addUserFollower(int userId, int userIdToFollow) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Não foi encontrado nenhum usuário cliente com o id: " + userId));
        Seller seller = sellerRepository.findById(userIdToFollow)
                .orElseThrow(() -> new NoSuchElementException("Não foi encontrado nenhum usuário vendedor com o id: " + userIdToFollow));
        boolean alreadyExists = seller.getFollowers().stream().anyMatch(c -> c == client);
        if (alreadyExists) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Registro já existe.");
        } else {
            seller.addFollower(client);
            sellerRepository.save(seller);
        }
    }

    public void removeUserFollower(int userId, int userIdToUnfollow) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Não foi encontrado nenhum usuário cliente com o id: " + userId));
        Seller seller = sellerRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new NoSuchElementException("Não foi encontrado nenhum usuário vendedor com o id: " + userIdToUnfollow));
        boolean alreadyExists = seller.getFollowers().stream().anyMatch(c -> c == client);
        if (!alreadyExists) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Registro não existe.");
        } else {
            seller.removeFollower(client);
            sellerRepository.save(seller);
        }
    }
}
