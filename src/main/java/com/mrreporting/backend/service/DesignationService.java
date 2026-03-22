package com.mrreporting.backend.service;

import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    public Designation saveDesignation(Designation designation) {
        // Saves the new designation to the database and returns the saved object
        return designationRepository.save(designation);
    }

    public List<Designation> getAllDesignations() {
        // 🌟 FIXED: Use the built-in method to fetch absolutely everything!
        return designationRepository.findAll();
    }

    public Designation updateDesignation(Long id, Designation updatedDetails) {
        Designation existingDesignation = designationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Designation not found with id: " + id));

        // Update the fields
        existingDesignation.setName(updatedDetails.getName());
        existingDesignation.setLevel(updatedDetails.getLevel());
        existingDesignation.setFullForm(updatedDetails.getFullForm());

        // Save and return the updated entity
        return designationRepository.save(existingDesignation);
    }

    public List<Designation> getDesignationsForHierarchy() {
        return designationRepository.findDesignationsBelowTopLevel();
    }
}