package com.GreenThumb.GT.controllers.MaterialExchangeControllers;

import com.GreenThumb.GT.DTO.ResourceExchangeDTO.ExchangeDTO;
import com.GreenThumb.GT.DTO.ResourceExchangeDTO.ResourceDTO;
import com.GreenThumb.GT.DTO.ResourceExchangeDTO.ResourceRequestDTO;
import com.GreenThumb.GT.DTO.ResourceExchangeDTO.SearchDTO;
import com.GreenThumb.GT.models.ResourceExchange.Resource.ResourceType;
import com.GreenThumb.GT.services.ResourceExchange.SearchService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("GreenThumb/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/filter")
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

        return ResponseEntity.ok().body(
                new SearchResults(resources, requests, exchanges)
        );
    }

    @Getter
    @AllArgsConstructor
    static class SearchResults {
        private List<ResourceDTO> resources;
        private List<ResourceRequestDTO> requests;
        private List<ExchangeDTO> exchanges;
    }
}
