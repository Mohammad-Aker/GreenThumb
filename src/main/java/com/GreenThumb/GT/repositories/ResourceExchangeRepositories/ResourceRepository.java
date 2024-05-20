package com.GreenThumb.GT.repositories.ResourceExchangeRepositories;

import com.GreenThumb.GT.models.ResourceExchange.Resource.Resource;
import com.GreenThumb.GT.models.ResourceExchange.Resource.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByTypeAndQuantityIsGreaterThan(ResourceType type, int quantity);

    //List<ResourceExchange> findByType(ResourceType type);

    List<Resource> findByQuantityIsGreaterThan(int zero);

}
