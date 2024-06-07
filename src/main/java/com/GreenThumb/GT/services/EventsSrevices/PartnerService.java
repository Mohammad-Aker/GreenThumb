package com.GreenThumb.GT.services.EventsSrevices;
import com.GreenThumb.GT.models.Events.Partner;
import com.GreenThumb.GT.repositories.EventsRepository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartnerService {

    @Autowired
    private PartnerRepository partnerRepository;

    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public Optional<Partner> getPartnerById(Long id) {
        return partnerRepository.findById(id);
    }

    public Partner createPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }
}
