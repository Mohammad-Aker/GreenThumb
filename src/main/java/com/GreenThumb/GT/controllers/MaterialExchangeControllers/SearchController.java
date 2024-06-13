package com.GreenThumb.GT.controllers.MaterialExchangeControllers;

import com.GreenThumb.GT.DTO.MaterialExchangeDTO.ExchangeDTO;
import com.GreenThumb.GT.DTO.MaterialExchangeDTO.MaterialDTO;
import com.GreenThumb.GT.DTO.MaterialExchangeDTO.MaterialRequestDTO;
import com.GreenThumb.GT.DTO.MaterialExchangeDTO.SearchDTO;
import com.GreenThumb.GT.models.MaterialExchange.Material.MaterialType;
import com.GreenThumb.GT.services.MaterialExchange.SearchService;
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
    public ResponseEntity<?> search(@RequestParam(required = false) MaterialType materialType,
                                    @RequestParam(required = false) String ownerEmail,
                                    @RequestParam(required = false) String requestStatus,
                                    @RequestParam(required = false) String exchangeStatus) {

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setMaterialType(materialType);
        searchDTO.setOwnerEmail(ownerEmail);
        searchDTO.setRequestStatus(requestStatus);
        searchDTO.setExchangeStatus(exchangeStatus);

        List<MaterialDTO> materials = searchService.searchMaterials(searchDTO);
        List<MaterialRequestDTO> requests = searchService.searchRequests(searchDTO);
        List<ExchangeDTO> exchanges = searchService.searchExchanges(searchDTO);

        return ResponseEntity.ok().body(
                new SearchResults(materials, requests, exchanges)
        );
    }

    @Getter
    @AllArgsConstructor
    static class SearchResults {
        private List<MaterialDTO> resources;
        private List<MaterialRequestDTO> requests;
        private List<ExchangeDTO> exchanges;
    }
}
