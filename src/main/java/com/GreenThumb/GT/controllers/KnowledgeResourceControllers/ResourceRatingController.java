package com.GreenThumb.GT.controllers.KnowledgeResourceControllers;

import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.RateResourceDTO;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.ReportResourceDTO;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.Views;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ReportResponse;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ReportedResourceResponse;
import com.GreenThumb.GT.response.KnowledgeResourceResponses.ResourceRatingResponse;
import com.GreenThumb.GT.services.KnowledgeResourceServices.ResourceRatingService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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







    ///////////////////////////////////////////  Rating  /////////////////////////////////////////////////



    @PostMapping("/rate")
    public ResponseEntity<?> rateResource(@AuthenticationPrincipal User user, @RequestBody RateResourceDTO request) {
        try {
            ResourceRatingResponse response = resourceRatingService.rateResource(request.getTitle(), user, request.getRating());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    ///////////////////////////////////////////  Reporting  /////////////////////////////////////////////////


    @PostMapping("/report")
    public ResponseEntity<?> reportResource(@AuthenticationPrincipal User user, @RequestBody ReportResourceDTO request) {
        try {
            ReportResponse response = resourceRatingService.reportResource(request.getTitle(), user, request.getReportDescription());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }



    ////////////////////////////////////////////// Get Ratings & Reports ///////////////////////////////////////////


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/ratings")
    public ResponseEntity<List<ResourceRatingResponse>> getRatingsForResource(@RequestParam String title) {
        List<ResourceRatingResponse> ratingsResponse = resourceRatingService.getRatingsForResource(title);
        return ResponseEntity.ok(ratingsResponse);
    }



    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/reports")
    public ResponseEntity<List<ReportedResourceResponse>> getReportedResources() {
        List<ReportedResourceResponse> reportedResources = resourceRatingService.getReportedResources();
        return ResponseEntity.ok(reportedResources);
    }



    //download >>needs error handling

    // dtos you fill them
}