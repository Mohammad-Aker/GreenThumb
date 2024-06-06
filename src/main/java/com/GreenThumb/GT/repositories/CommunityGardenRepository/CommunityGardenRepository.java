package com.GreenThumb.GT.repositories.CommunityGardenRepository;
import com.GreenThumb.GT.models.CommunityGarden.CommunityGarden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityGardenRepository extends JpaRepository<CommunityGarden, Long> {
    // Add any custom query methods if needed
}
