package com.GreenThumb.GT.controllers.EventsControllers;
import com.GreenThumb.GT.models.Events.Partner;
import com.GreenThumb.GT.services.EventsSrevices.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/partners")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @GetMapping
    public List<Partner> getAllPartners() {
        return partnerService.getAllPartners();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partner> getPartnerById(@PathVariable Long id) {
        Optional<Partner> partner = partnerService.getPartnerById(id);
        return partner.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Partner> createPartner(@RequestBody Partner partner) {
        Partner createdPartner = partnerService.createPartner(partner);
        return ResponseEntity.ok(createdPartner);
    }
}
