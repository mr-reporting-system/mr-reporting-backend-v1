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
        // Only fetch these specific roles
        return designationRepository.findByNameIn(List.of("Manager", "MR"));
    }
}