package com.GreenThumb.GT.repositories.MaterialExchangeRepositories;

import com.GreenThumb.GT.models.MaterialExchange.Material.Material;
import com.GreenThumb.GT.models.MaterialExchange.Material.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByTypeAndQuantityIsGreaterThan(MaterialType type, int quantity);

    //List<ResourceExchange> findByType(ResourceType type);

    List<Material> findByQuantityIsGreaterThan(int zero);

}
