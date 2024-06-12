package com.GreenThumb.GT.services.CommunityGardenServices;

import com.GreenThumb.GT.models.CommunityGarden.CommunityGarden;
import com.GreenThumb.GT.models.CommunityGarden.GardenStatus;
import com.GreenThumb.GT.models.CommunityGarden.UserCommunityGarden;
import com.GreenThumb.GT.repositories.CommunityGardenRepository.CommunityGardenRepository;
import com.GreenThumb.GT.repositories.CommunityGardenRepository.UserCommunityGardenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityGardenService {

    @Autowired
    private CommunityGardenRepository communityGardenRepository;

    @Autowired
    private UserCommunityGardenRepository userCommunityGardenRepository;



    public List<CommunityGarden> getAllApprovedGardens() {
        return communityGardenRepository.findAll().stream()
                .filter(garden -> garden.getStatus() == GardenStatus.APPROVED)
                .collect(Collectors.toList());
    }

    public List<CommunityGarden> getAllGardens() {
        return communityGardenRepository.findAll();
    }

    public List<CommunityGarden> getAllPendingGardens() {
        return communityGardenRepository.findAll().stream()
                .filter(garden -> garden.getStatus() == GardenStatus.PENDING)
                .collect(Collectors.toList());
    }
    public Optional<CommunityGarden> getGardenById(Long id) {
        Optional<CommunityGarden> garden = communityGardenRepository.findById(id);
        return garden.filter(g -> g.getStatus() == GardenStatus.APPROVED);
    }

    public CommunityGarden createGarden(CommunityGarden communityGarden) {
        communityGarden.setStatus(GardenStatus.PENDING); // Set status to pending
        return communityGardenRepository.save(communityGarden);
    }

    public CommunityGarden updateGarden(Long id, CommunityGarden communityGardenDetails) {
        return communityGardenRepository.findById(id).map(garden -> {
            garden.setName(communityGardenDetails.getName());
            garden.setLocation(communityGardenDetails.getLocation());
            garden.setDescription(communityGardenDetails.getDescription());
            garden.setAvailablePlots(communityGardenDetails.getAvailablePlots());
            garden.setEstablishmentYear(communityGardenDetails.getEstablishmentYear());
            garden.setSocialMediaLinks(communityGardenDetails.getSocialMediaLinks());
            garden.setRulesAndRegulations(communityGardenDetails.getRulesAndRegulations());
            return communityGardenRepository.save(garden);
        }).orElseGet(() -> {
            communityGardenDetails.setId(id);
            return communityGardenRepository.save(communityGardenDetails);
        });
    }

    public void deleteGarden(Long id) {
        communityGardenRepository.deleteById(id);
    }

    public CommunityGarden approveGarden(Long id) {
        return communityGardenRepository.findById(id).map(garden -> {
            garden.setStatus(GardenStatus.APPROVED);
            return communityGardenRepository.save(garden);
        }).orElseThrow(() -> new RuntimeException("Garden not found"));
    }

    public CommunityGarden rejectGarden(Long id) {
        return communityGardenRepository.findById(id).map(garden -> {
            garden.setStatus(GardenStatus.REJECTED);
            return communityGardenRepository.save(garden);
        }).orElseThrow(() -> new RuntimeException("Garden not found"));
    }


    public void leaveGarden(Long gardenId, String userEmail) {
        // Find the user-community garden association for the given user
        UserCommunityGarden userCommunityGarden = (UserCommunityGarden) userCommunityGardenRepository.findByUserEmailAndCommunityGardenId(userEmail, gardenId)
                .orElseThrow(() -> new IllegalArgumentException("UserCommunityGarden not found for user " + userEmail + " in garden " + gardenId));

        userCommunityGardenRepository.delete(userCommunityGarden);
    }


    public void joinGarden(Long gardenId, UserCommunityGarden userCommunityGarden) {
        CommunityGarden garden = communityGardenRepository.findCommunityGardenByIdAndStatus(gardenId,GardenStatus.APPROVED)
                .orElseThrow(() -> new IllegalArgumentException("Community Garden not found with id: " + gardenId));
        userCommunityGarden.setCommunityGarden(garden);
        userCommunityGardenRepository.save(userCommunityGarden);
    }



}
