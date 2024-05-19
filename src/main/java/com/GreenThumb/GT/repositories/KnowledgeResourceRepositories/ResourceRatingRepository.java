package com.GreenThumb.GT.repositories.KnowledgeResourceRepositories;

import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.ResourceRating.ResourceRating;
import com.GreenThumb.GT.models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRatingRepository extends JpaRepository<ResourceRating, Long> {

  List<ResourceRating> findByReportedTrue();
  List<ResourceRating> findByResource_Title(String title);
  boolean existsByUserAndResource(User user, KnowledgeResource resource);
  boolean existsByUserAndResourceAndReportedIsTrue(User user, KnowledgeResource resource);
  ResourceRating findByResourceAndUser( User user,KnowledgeResource resource);

}
