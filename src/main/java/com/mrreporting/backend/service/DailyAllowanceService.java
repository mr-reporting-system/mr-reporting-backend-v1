package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.DailyAllowanceCreateDTO;
import com.mrreporting.backend.dto.DailyAllowanceResponseDTO;
import com.mrreporting.backend.dto.DailyAllowanceUpdateDTO;
import com.mrreporting.backend.entity.DailyAllowance;
import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.entity.State;
import com.mrreporting.backend.repository.DailyAllowanceRepository;
import com.mrreporting.backend.repository.DesignationRepository;
import com.mrreporting.backend.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyAllowanceService {

    @Autowired
    private DailyAllowanceRepository dailyAllowanceRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DesignationRepository designationRepository;

    // bulk create: stateIds.size() x designationIds.size() records saved in one call.
    // if a rule for a state+designation already exists, it is skipped with a warning
    // instead of throwing an error — this prevents one duplicate from blocking the whole batch.
    @Transactional
    public List<DailyAllowanceResponseDTO> createDailyAllowances(DailyAllowanceCreateDTO dto) {
        if (dto.getStateIds() == null || dto.getStateIds().isEmpty()) {
            throw new IllegalArgumentException("At least one state must be selected.");
        }
        if (dto.getDesignationIds() == null || dto.getDesignationIds().isEmpty()) {
            throw new IllegalArgumentException("At least one designation must be selected.");
        }
        if (dto.getHqDa() == null || dto.getExDa() == null || dto.getOutDa() == null) {
            throw new IllegalArgumentException("HQ DA, EX DA, and OUT DA are required.");
        }

        List<State> states = stateRepository.findAllById(dto.getStateIds());
        List<Designation> designations = designationRepository.findAllById(dto.getDesignationIds());

        if (states.size() != dto.getStateIds().size()) {
            throw new RuntimeException("One or more states not found.");
        }
        if (designations.size() != dto.getDesignationIds().size()) {
            throw new RuntimeException("One or more designations not found.");
        }

        List<DailyAllowance> toSave = new ArrayList<>();

        for (State state : states) {
            for (Designation designation : designations) {
                // skip if a rule already exists for this combination
                boolean alreadyExists = dailyAllowanceRepository
                        .findByStateIdAndDesignationId(state.getId(), designation.getId())
                        .isPresent();

                if (alreadyExists) {
                    continue;
                }

                DailyAllowance da = new DailyAllowance();
                da.setState(state);
                da.setDesignation(designation);
                mapDtoToEntity(dto, da);
                toSave.add(da);
            }
        }

        if (toSave.isEmpty()) {
            throw new RuntimeException("All selected state-designation combinations already have a Daily Allowance rule.");
        }

        List<DailyAllowance> saved = dailyAllowanceRepository.saveAll(toSave);
        return saved.stream().map(this::toResponseDTO).toList();
    }

    // returns all DA records for the table
    public List<DailyAllowanceResponseDTO> getAllDailyAllowances() {
        return dailyAllowanceRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // updates a single DA record — triggered when admin clicks edit in the table
    @Transactional
    public DailyAllowanceResponseDTO updateDailyAllowance(Long id, DailyAllowanceUpdateDTO dto) {
        DailyAllowance da = dailyAllowanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Daily allowance not found with id: " + id));

        da.setHqDa(dto.getHqDa());
        da.setExDa(dto.getExDa());
        da.setOutDa(dto.getOutDa());
        da.setMobileAllowance(nullSafe(dto.getMobileAllowance()));
        da.setNetAllowance(nullSafe(dto.getNetAllowance()));
        da.setPostageStationary(nullSafe(dto.getPostageStationary()));
        da.setPostageFreight(nullSafe(dto.getPostageFreight()));

        return toResponseDTO(dailyAllowanceRepository.save(da));
    }

    // hard deletes a single DA record
    @Transactional
    public void deleteDailyAllowance(Long id) {
        if (!dailyAllowanceRepository.existsById(id)) {
            throw new RuntimeException("Daily allowance not found with id: " + id);
        }
        dailyAllowanceRepository.deleteById(id);
    }

    // maps create DTO fields onto a DA entity, using zero as default for optional fields
    private void mapDtoToEntity(DailyAllowanceCreateDTO dto, DailyAllowance da) {
        da.setHqDa(dto.getHqDa());
        da.setExDa(dto.getExDa());
        da.setOutDa(dto.getOutDa());
        da.setMobileAllowance(nullSafe(dto.getMobileAllowance()));
        da.setNetAllowance(nullSafe(dto.getNetAllowance()));
        da.setPostageStationary(nullSafe(dto.getPostageStationary()));
        da.setPostageFreight(nullSafe(dto.getPostageFreight()));
    }

    // converts entity to response DTO and calculates special allowance on the fly
    private DailyAllowanceResponseDTO toResponseDTO(DailyAllowance da) {
        BigDecimal specialAllowance = nullSafe(da.getPostageStationary())
                .add(nullSafe(da.getPostageFreight()));

        return new DailyAllowanceResponseDTO(
                da.getId(),
                da.getState().getId(),
                da.getState().getStateName(),
                da.getDesignation().getId(),
                da.getDesignation().getName(),
                da.getHqDa(),
                da.getExDa(),
                da.getOutDa(),
                nullSafe(da.getMobileAllowance()),
                nullSafe(da.getNetAllowance()),
                specialAllowance
        );
    }

    // treats null as zero to avoid NullPointerException during addition
    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}