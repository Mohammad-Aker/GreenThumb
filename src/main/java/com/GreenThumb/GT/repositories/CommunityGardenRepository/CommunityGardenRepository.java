package com.GreenThumb.GT.repositories.CommunityGardenRepository;
import com.GreenThumb.GT.models.CommunityGarden.CommunityGarden;
import com.GreenThumb.GT.models.CommunityGarden.GardenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityGardenRepository extends JpaRepository<CommunityGarden, Long> {

    Optional <CommunityGarden> findCommunityGardenByIdAndStatus(Long id, GardenStatus status);

}
