package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.DTO.*;
import com.GreenThumb.GT.models.Resource.ResourceType;
import com.GreenThumb.GT.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GreenThumb/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<?> search(@RequestParam(required = false) ResourceType resourceType,
                                    @RequestParam(required = false) String ownerEmail,
                                    @RequestParam(required = false) String requestStatus,
                                    @RequestParam(required = false) String exchangeStatus) {

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setResourceType(resourceType);
        searchDTO.setOwnerEmail(ownerEmail);
        searchDTO.setRequestStatus(requestStatus);
        searchDTO.setExchangeStatus(exchangeStatus);

        List<ResourceDTO> resources = searchService.searchResources(searchDTO);
        List<ResourceRequestDTO> requests = searchService.searchRequests(searchDTO);
        List<ExchangeDTO> exchanges = searchService.searchExchanges(searchDTO);

        // Ensure requests and exchanges are filtered appropriately based on the criteria
        if (searchDTO.getResourceType() != null) {
            requests = requests.stream()
                    .filter(request -> request.getResourceType() == searchDTO.getResourceType())
                    .collect(Collectors.toList());
            exchanges = exchanges.stream()
                    .filter(exchange -> exchange.getResourceType() == searchDTO.getResourceType())
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok().body(
                new SearchResults(resources, requests, exchanges)
        );
    }

    static class SearchResults {
        private List<ResourceDTO> resources;
        private List<ResourceRequestDTO> requests;
        private List<ExchangeDTO> exchanges;

        public SearchResults(List<ResourceDTO> resources, List<ResourceRequestDTO> requests, List<ExchangeDTO> exchanges) {
            this.resources = resources;
            this.requests = requests;
            this.exchanges = exchanges;
        }

        public List<ResourceDTO> getResources() {
            return resources;
        }

        public List<ResourceRequestDTO> getRequests() {
            return requests;
        }

        public List<ExchangeDTO> getExchanges() {
            return exchanges;
        }
    }
}
