package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.payload.Rates.RateResourcePayload;
import com.GreenThumb.GT.payload.Rates.ReportResourcePayload;
import com.GreenThumb.GT.payload.Rates.Views;
import com.GreenThumb.GT.response.ReportedResourceResponse;
import com.GreenThumb.GT.response.ResourceRatingResponse;
import com.GreenThumb.GT.services.ResourceRatingService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.GreenThumb.GT.services.ResourceRatingService.logger;


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
    public ResponseEntity<?> rateResource(@AuthenticationPrincipal User user, @RequestBody RateResourcePayload request) {
        try {
            ResourceRatingResponse response = resourceRatingService.rateResource(request.getTitle(), user, request.getRating());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    //except the creator
    @PostMapping("/report")
    public ResponseEntity<?> reportResource(@AuthenticationPrincipal User user, @RequestBody ReportResourcePayload request) {
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