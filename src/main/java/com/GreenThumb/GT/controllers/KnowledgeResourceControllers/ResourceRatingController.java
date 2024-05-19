package com.GreenThumb.GT.controllers.KnowledgeResourceControllers;

import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.RateResourceDTO;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.ReportResourceDTO;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.Views;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ReportedResourceResponse;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ResourceRatingResponse;
import com.GreenThumb.GT.services.KnowledgeResourceServices.ResourceRatingService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/rating")
public class ResourceRatingController {

    private final ResourceRatingService resourceRatingService;


    @Autowired
    public ResourceRatingController(ResourceRatingService resourceRatingService){
        this.resourceRatingService = resourceRatingService;}



    //admin
    //all reports
    @GetMapping("/reported")
    public ResponseEntity<List<ReportedResourceResponse>> getReportedResources() {
        List<ReportedResourceResponse> reportedResources = resourceRatingService.getReportedResources();
        return ResponseEntity.ok(reportedResources);
    }


    //except the creator
    @PostMapping("/rate")
    public ResponseEntity<?> rateResource(@AuthenticationPrincipal User user, @RequestBody RateResourceDTO request) {
        try {
            ResourceRatingResponse response = resourceRatingService.rateResource(request.getTitle(), user, request.getRating());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    //except the creator
    @PostMapping("/report")
    public ResponseEntity<?> reportResource(@AuthenticationPrincipal User user, @RequestBody ReportResourceDTO request) {
        try {
            ResourceRatingResponse response = resourceRatingService.reportResource(request.getTitle(),user, request.getReportDescription());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    //admin
    @JsonView(Views.Internal.class)
    @GetMapping("/ratings")
    public ResponseEntity<List<ResourceRatingResponse>> getRatingsForResource(@RequestParam String title) {
        List<ResourceRatingResponse> ratingsResponse = resourceRatingService.getRatingsForResource(title);
        return ResponseEntity.ok(ratingsResponse);
    }



}