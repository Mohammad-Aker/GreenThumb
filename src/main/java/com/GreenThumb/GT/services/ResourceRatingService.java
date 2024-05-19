package com.GreenThumb.GT.services;

import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.ResourceRating.ResourceRating;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.response.ReportedResourceResponse;
import com.GreenThumb.GT.repositories.UserRepository;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.repositories.ResourceRatingRepository;
import com.GreenThumb.GT.response.ResourceRatingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceRatingService {

    public static final Logger logger = LoggerFactory.getLogger(ResourceRatingService.class);

    private final ResourceRatingRepository resourceRatingRepository;
    private final KnowledgeResourceRepository knowledgeResourceRepository;
    private final UserRepository userRepository;


    @Autowired
    public ResourceRatingService(ResourceRatingRepository resourceRatingRepository,
                                 KnowledgeResourceRepository knowledgeResourceRepository,
                                 UserRepository userRepository) {
        this.resourceRatingRepository = resourceRatingRepository;
        this.knowledgeResourceRepository = knowledgeResourceRepository;
        this.userRepository = userRepository;
    }

    public ResourceRatingResponse rateResource(String title, User user, Double rating) {
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }

        KnowledgeResource resource = knowledgeResourceRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("No resource found with title: " + title));

        // Check if the user has already rated this resource
        boolean ratingExists = resourceRatingRepository.existsByUserAndResource(user, resource);
        if (ratingExists) {
            throw new IllegalArgumentException("You have already rated this resource");
        }
        boolean reportExists = resourceRatingRepository.existsByUserAndResourceAndReportedIsTrue(user, resource);
        if (reportExists) {
            throw new IllegalArgumentException("You have already reported this resource");
        }

        ResourceRating ratingEntity = new ResourceRating();
        ratingEntity.setResource(resource);
        ratingEntity.setUser(user);
        ratingEntity.setRating(rating);
        ratingEntity.setReported(false);

        ratingEntity = resourceRatingRepository.save(ratingEntity);
        updateAverageRating(resource);

        return convertToRatingResponse(ratingEntity);
    }

    private ResourceRatingResponse convertToRatingResponse(ResourceRating rating) {
        return ResourceRatingResponse.builder()
                .id(rating.getId())
                .resourceTitle(rating.getResource().getTitle())
                .userEmail(rating.getUser().getEmail())
                .rating(rating.getRating())
                .createdDate(rating.getCreatedDate())
                .build();
    }

    public ResourceRatingResponse reportResource(String title, User user, String description) {
        KnowledgeResource resource = knowledgeResourceRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("No resource found with title: " + title));
        ResourceRating reportEntity = resourceRatingRepository.findByResourceAndUser(user, resource);

        // Check if the user has already reported this resource
        boolean reportExists = resourceRatingRepository.existsByUserAndResourceAndReportedIsTrue(user, resource);
        if (reportExists) {
            throw new IllegalArgumentException("You have already reported this resource");
        } else {
            boolean ratingExists = resourceRatingRepository.existsByUserAndResource(user, resource);
            if (ratingExists) {

                reportEntity.setRating(0.0);
                reportEntity.setReportDescription(description);
                reportEntity.setReported(true);
                reportEntity = resourceRatingRepository.save(reportEntity);
                // Update average rating of the resource
                updateAverageRating(reportEntity.getResource());


            }
             else return convertToReportResponse(reportEntity);

        }
        return convertToReportResponse(reportEntity);
    }


    private ResourceRatingResponse convertToReportResponse(ResourceRating report) {
        return new ResourceRatingResponse(report.getId(), report.getResource().getTitle(), report.getUser().getEmail(), 0.0, report.getReportDescription(),report.getCreatedDate());
    }

    private void updateAverageRating(KnowledgeResource resource) {
        double averageRating = resource.getRatings().stream()
                .filter(rating -> !rating.isReported())
                .mapToDouble(ResourceRating::getRating)
                .average()
                .orElse(0.0);
        resource.setAverageRating(averageRating);
        knowledgeResourceRepository.save(resource);
    }

    public List<ReportedResourceResponse> getReportedResources() {
        List<ResourceRating> reportedRatings = resourceRatingRepository.findByReportedTrue();
        return reportedRatings.stream()
                .map(rating -> {
                    KnowledgeResource resource = rating.getResource();
                    return ReportedResourceResponse.builder()
                            .userEmail(rating.getUser().getEmail())
                            .category(resource.getCategory())
                            .resourceTitle(resource.getTitle())
                            .author(resource.getAuthor())
                            .type(resource.getType())
                            .content(resource.getContentUrl())
                            .reportDescription(rating.getReportDescription())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<ResourceRatingResponse> getRatingsForResource(String resourceTitle) {
        List<ResourceRating> ratings = resourceRatingRepository.findByResource_Title(resourceTitle);
        return ratings.stream()
                .map(rating -> ResourceRatingResponse.builder()
                        .id(rating.getId())
                        .resourceTitle(rating.getResource().getTitle())
                        .userEmail(rating.getUser().getEmail())
                        .rating(rating.getRating())
                        .createdDate(rating.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

}


