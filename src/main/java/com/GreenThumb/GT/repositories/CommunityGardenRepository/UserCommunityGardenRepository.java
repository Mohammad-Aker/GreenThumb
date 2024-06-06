package com.GreenThumb.GT.repositories.CommunityGardenRepository;

import com.GreenThumb.GT.models.CommunityGarden.UserCommunityGarden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCommunityGardenRepository extends JpaRepository<UserCommunityGarden, Long> {
    Optional<Object> findByCommunityGardenIdAndUserEmail(Long gardenId, String userEmail);

    Optional<Object> findByUserEmailAndCommunityGardenId(String userEmail, long gardenId);
}
