package com.GreenThumb.GT.repositories;

import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


//this interface is responsible for data access
@Repository
public interface KnowledgeResourceRepository extends JpaRepository<KnowledgeResource, Long> {


  Optional<KnowledgeResource> findByTitle(String title);

  List<KnowledgeResource> findByCategory(ResourceCategory category);

  boolean existsByTitle(String title);

  boolean existsByContentUrl(String contentUrl);
}




