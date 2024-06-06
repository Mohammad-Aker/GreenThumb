package com.GreenThumb.GT.services.KnowledgeResourceServices;

import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.ResourceRating.ResourceRating;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ReportResponse;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ReportedResourceResponse;
import com.GreenThumb.GT.repositories.UserRepository;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepositories.ResourceRatingRepository;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ResourceRatingResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceRatingService {

    public static final Logger logger = LoggerFactory.getLogger(ResourceRatingService.class);

    private final ResourceRatingRepository resourceRatingRepository;
    private final KnowledgeResourceRepository knowledgeResourceRepository;



    @Autowired
    public ResourceRatingService(ResourceRatingRepository resourceRatingRepository,
                                 KnowledgeResourceRepository knowledgeResourceRepository,
                                 UserRepository userRepository) {
        this.resourceRatingRepository = resourceRatingRepository;
        this.knowledgeResourceRepository = knowledgeResourceRepository;

    }



    ///////////////////////////////////////////  Rating  /////////////////////////////////////////////////

    @Transactional
    public ResourceRatingResponse rateResource(String title, User user, Double rating) {
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }

        KnowledgeResource resource = knowledgeResourceRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("No resource found with title: " + title));

        if (resource.getUser().getEmail().equals(user.getEmail())) {
            throw new KnowledgeResourceService.UnauthorizedException("You can't rate a resource you uploaded");
        }

        Optional<ResourceRating> existingRatingOpt = resourceRatingRepository.findByUserAndResource(user, resource);

        if (existingRatingOpt.isPresent()) {
            ResourceRating existingRating = existingRatingOpt.get();
            if (existingRating.isReported()) {
                throw new IllegalStateException("You cannot rate a resource you have reported.");
            }
            existingRating.setRating(rating); // Update existing rating instead of creating a new one
            resourceRatingRepository.save(existingRating);
            updateAverageRating(resource);
            return convertToRatingResponse(existingRating);
        } else {
            ResourceRating newRating = new ResourceRating();
            newRating.setUser(user);
            newRating.setResource(resource);
            newRating.setCreatedDate(new Date());  // Set the created date for new ratings
            newRating.setRating(rating);
            newRating.setReported(false);  // Initialize as false for new ratings

            resourceRatingRepository.save(newRating);
            updateAverageRating(resource);
            return convertToRatingResponse(newRating);
        }
    }

    private ResourceRatingResponse convertToRatingResponse(ResourceRating ratingEntity) {
        ResourceRatingResponse response = new ResourceRatingResponse();
        //response.setId(ratingEntity.getId());
        response.setResourceTitle(ratingEntity.getResource().getTitle());
        response.setUserEmail(ratingEntity.getUser().getEmail());
        response.setRating(ratingEntity.getRating());
       // response.setReportDescription(ratingEntity.getReportDescription());  // Ensure this is managed appropriately in your entity
        response.setCreatedDate(ratingEntity.getCreatedDate());
        return response;
    }


    ///////////////////////////////////////////  Reporting  /////////////////////////////////////////////////


    @Transactional
    public ReportResponse reportResource(String title, User user, String reportDescription) {
        KnowledgeResource resource = knowledgeResourceRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("No resource found with title: " + title));

        if (resource.getUser().getEmail().equals(user.getEmail())) {
            throw new KnowledgeResourceService.UnauthorizedException("You can't report a resource you uploaded");
        }
        // Check if the user has already reported this resource
        Optional<ResourceRating> existingRatingOpt = resourceRatingRepository.findByUserAndResource(user, resource);

        if (existingRatingOpt.isPresent() && existingRatingOpt.get().isReported()) {
            throw new IllegalStateException("You have already reported this resource.");
        }

        ResourceRating rating = existingRatingOpt.orElseGet(() -> {
            ResourceRating newRating = new ResourceRating();
            newRating.setUser(user);
            newRating.setResource(resource);
            newRating.setCreatedDate(new Date());
            return newRating;
        });
        // Mark the rating as reported and update details
        rating.setRating(0.0); // Set rating to 0 when reporting
        rating.setReported(true);
        rating.setReportDescription(reportDescription);

        resourceRatingRepository.save(rating);
        updateAverageRating(resource);  // Update average rating to reflect the new report

        return convertToReportResponse(rating);
    }

    private ReportResponse convertToReportResponse(ResourceRating ratingEntity) {
        ReportResponse response = new ReportResponse();
        //response.setId(ratingEntity.getId());
        response.setResourceTitle(ratingEntity.getResource().getTitle());
        response.setUserEmail(ratingEntity.getUser().getEmail());
        response.setReportDescription(ratingEntity.getReportDescription());  // Ensure this is managed appropriately in your entity
        response.setCreatedDate(ratingEntity.getCreatedDate());
        return response;

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


    //the user can rate multiple times
    //the user can report a resource once
    //the user can't rate after report or rate
    //the expert can't rate or report his resources


    ////////////////////////////////////////////// Get Ratings & Reports ///////////////////////////////////////////


    //for Admin

    //test them

    public List<ResourceRatingResponse> getRatingsForResource(String resourceTitle) {
        List<ResourceRating> ratings = resourceRatingRepository.findByResource_Title(resourceTitle);
        if (ratings.isEmpty()) {
            throw new ResourceNotFoundException("No ratings found for the title: " + resourceTitle);
        }

        return ratings.stream()
                .map(rating -> ResourceRatingResponse.builder()
                        .resourceTitle(rating.getResource().getTitle())
                        .userEmail(rating.getUser().getEmail())
                        .rating(rating.getRating())
                        .createdDate(rating.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
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
                          //  .content(resource.getContentUrl())
                            .reportDescription(rating.getReportDescription())
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void clearAllReports() {
        // Retrieve all ratings that have been reported
        List<ResourceRating> reportedRatings = resourceRatingRepository.findAllByReportedIsTrue();

        if (reportedRatings.isEmpty()) {
            throw new IllegalStateException("No reported resources found.");
        }

        // Set reported to false and clear report descriptions for all reported ratings
        reportedRatings.forEach(rating -> {
            rating.setReported(false);
            rating.setReportDescription(null);
        });

        // Save the updated ratings
        resourceRatingRepository.saveAll(reportedRatings);

        // Optionally, update average ratings for each affected resource
        reportedRatings.stream()
                .map(ResourceRating::getResource)
                .distinct()
                .forEach(this::updateAverageRating);
    }

}

