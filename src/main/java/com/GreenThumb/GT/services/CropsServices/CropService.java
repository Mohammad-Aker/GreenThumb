package com.GreenThumb.GT.services.CropsServices;

import com.GreenThumb.GT.models.CropsTracking.Crop;
import com.GreenThumb.GT.repositories.CropsRepositories.CropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CropService {

    private final CropRepository cropRepository;

    @Autowired
    public CropService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public Crop getCropById(Long id) {
        System.out.println(id);
        Optional<Crop> cropOptional = cropRepository.findById(id);
        return cropOptional.orElse(null);
    }

    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }
    public Crop saveCrop(Crop crop) {
        return cropRepository.save(crop);
    }

    public void deleteCrop(Long id) {
        cropRepository.deleteById(id);
    }
}
