package com.GreenThumb.GT.controllers.CommunityGarden;

import com.GreenThumb.GT.models.CommunityGarden.CommunityGarden;
import com.GreenThumb.GT.models.CommunityGarden.UserCommunityGarden;
import com.GreenThumb.GT.services.CommunityGardenServices.CommunityGardenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/GreenThumb/api/community-gardens")
public class CommunityGardenController {

    @Autowired
    private CommunityGardenService communityGardenService;

    @GetMapping
   // @PreAuthorize("hasAnyRole('USER', 'EXPERT', 'ADMIN')")
    public List<CommunityGarden> getAllGardens() {
        return communityGardenService.getAllGardens();
    }


    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('USER', 'EXPERT', 'ADMIN')")
    public ResponseEntity<CommunityGarden> getGardenById(@PathVariable Long id) {
        Optional<CommunityGarden> garden = communityGardenService.getGardenById(id);
        return garden.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    //@PreAuthorize("hasAnyRole('USER', 'EXPERT')")
    public ResponseEntity<CommunityGarden> createGarden(@RequestBody CommunityGarden communityGarden) {
        CommunityGarden createdGarden = communityGardenService.createGarden(communityGarden);
        return ResponseEntity.ok(createdGarden);
    }

    @PutMapping("/{id}")
   // @PreAuthorize("hasAnyRole('USER', 'EXPERT')")
    public ResponseEntity<CommunityGarden> updateGarden(@PathVariable Long id, @RequestBody CommunityGarden communityGardenDetails) {
        CommunityGarden updatedGarden = communityGardenService.updateGarden(id, communityGardenDetails);
        return ResponseEntity.ok(updatedGarden);
    }

    @DeleteMapping("/{id}")
   // @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteGarden(@PathVariable Long id) {
        communityGardenService.deleteGarden(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityGarden> approveGarden(@PathVariable Long id) {
        CommunityGarden approvedGarden = communityGardenService.approveGarden(id);
        return ResponseEntity.ok(approvedGarden);
    }

    @PutMapping("/{id}/reject")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityGarden> rejectGarden(@PathVariable Long id) {
        CommunityGarden rejectedGarden = communityGardenService.rejectGarden(id);
        return ResponseEntity.ok(rejectedGarden);
    }


    //FOR JOINING A GARDENING ACTIVITY
    @PostMapping("/{gardenId}/join")
    //@PreAuthorize("hasAnyRole('USER', 'EXPERT')")
    public ResponseEntity<Void> joinGarden(@PathVariable Long gardenId, @RequestBody UserCommunityGarden userCommunityGarden) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) ((org.springframework.security.core.Authentication) authentication).getPrincipal()).getUsername();
        // Set the email in the userCommunityGarden object if needed
        userCommunityGarden.getUser().setEmail(userEmail);

        communityGardenService.joinGarden(gardenId, userCommunityGarden);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{gardenId}/delete")
    //@PreAuthorize("hasAnyRole('USER', 'EXPERT')")
    public ResponseEntity<Void> leaveGarden(@PathVariable Long gardenId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) ((org.springframework.security.core.Authentication) authentication).getPrincipal()).getUsername();
        communityGardenService.leaveGarden(gardenId, userEmail);
        return ResponseEntity.ok().build();
    }


}
