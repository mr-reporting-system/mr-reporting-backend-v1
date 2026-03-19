package com.mrreporting.backend.service;

import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    // --- Filtered view for Area/Doctor/Chemist Creation ---
    public List<State> getActiveStates() {
        return stateRepository.findByIsActiveTrue();
    }

    // --- Global view for Employee Creation 👈 (New method to get ALL) ---
    public List<State> getAllStates() {
        return stateRepository.findAll();
    }
}