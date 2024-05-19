package com.GreenThumb.GT.repositories.CropsRepositories;

import com.GreenThumb.GT.models.CropsTracking.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
}

