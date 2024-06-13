package com.GreenThumb.GT.repositories.MaterialExchangeRepositories;

import com.GreenThumb.GT.models.MaterialExchange.MaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Long> {
    List<MaterialRequest> findByUserEmail(String userEmail);

}
