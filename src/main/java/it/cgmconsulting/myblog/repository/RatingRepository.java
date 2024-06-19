package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Rating;
import it.cgmconsulting.myblog.entity.RatingId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {

//    @Modifying
//    // quando effettuo una query che non sia una select devo utilizzare le annotation @Modifying e @Transactional
//    @Transactional
//    @Query(value = "DELETE FROM rating " +
//            "WHERE post_id = :postId " +
//            "AND user_id = :userId", nativeQuery = true)
//    void deleteRating(int postId, int userId);


    @Query(value="SELECT COALESCE (rate) FROM rating " +
            "WHERE post_id = :postId " +
            "AND user_id = :userId", nativeQuery = true)
    Byte getMyRate(int postId, int userId);

}
