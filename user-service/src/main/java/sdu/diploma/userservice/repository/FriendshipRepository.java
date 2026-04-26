package sdu.diploma.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sdu.diploma.userservice.entity.Friendship;
import sdu.diploma.userservice.enums.FriendshipStatus;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :userId OR f.addresseeId = :userId) AND f.status = :status")
    List<Friendship> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :u1 AND f.addresseeId = :u2) OR (f.requesterId = :u2 AND f.addresseeId = :u1)")
    Optional<Friendship> findBetweenUsers(@Param("u1") Long user1, @Param("u2") Long user2);

    boolean existsByRequesterIdAndAddresseeId(Long requesterId, Long addresseeId);
}
