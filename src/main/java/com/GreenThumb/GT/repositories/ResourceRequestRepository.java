package com.GreenThumb.GT.repositories;

import com.GreenThumb.GT.models.ResourceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findByUserEmail(String userEmail);

}
