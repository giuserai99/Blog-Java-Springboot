package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.Rating;
import it.cgmconsulting.myblog.entity.RatingId;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final PostService postService;

    public byte addRate(UserDetails userDetails, int postId, byte rate){
        User user = (User) userDetails;
        Post post = postService.findPostById(postId);
        Rating r = new Rating(new RatingId(user, post), rate);
        ratingRepository.save(r);
        return r.getRate();
    }


    public void deleteRate(UserDetails userDetails, int postId){
        User user = (User) userDetails;
        Post post = postService.findPostById(postId);
        RatingId rId = new RatingId(user, post);
        ratingRepository.deleteById(rId);


        // cancellazione secca: non controllo se esiste il record, cancello e basta
        //ratingRepository.deleteRating(postId, user.getId());
    }

    public Byte getMyRate(UserDetails userDetails, int postId) {
        // recuperare il voto dato via SQL NATIVO
        // qualora l'utente non abbia votato, restituire 0

        User user = (User) userDetails;
        Post post = postService.findPostById(postId);
        Byte rate = ratingRepository.getMyRate(postId, user.getId());
        if(rate == null)
            return 0;
        return rate;
    }



}
