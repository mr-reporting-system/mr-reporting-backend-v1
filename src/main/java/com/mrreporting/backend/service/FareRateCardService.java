package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.FareRateCardCreateDTO;
import com.mrreporting.backend.dto.FareRateCardResponseDTO;
import com.mrreporting.backend.dto.FareRateCardRowDTO;
import com.mrreporting.backend.dto.FareRateCardUpdateDTO;
import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.entity.FareRateCard;
import com.mrreporting.backend.repository.DesignationRepository;
import com.mrreporting.backend.repository.FareRateCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FareRateCardService {

    @Autowired
    private FareRateCardRepository fareRateCardRepository;

    @Autowired
    private DesignationRepository designationRepository;

    // bulk create: for each designation × each row, save one FareRateCard record.
    // if admin picks 3 designations and adds 2 rows, this saves 6 records.
    @Transactional
    public List<FareRateCardResponseDTO> createFareRateCards(FareRateCardCreateDTO dto) {
        if (dto.getDesignationIds() == null || dto.getDesignationIds().isEmpty()) {
            throw new IllegalArgumentException("At least one designation must be selected.");
        }
        if (dto.getRows() == null || dto.getRows().isEmpty()) {
            throw new IllegalArgumentException("At least one rule row must be provided.");
        }

        List<Designation> designations = designationRepository.findAllById(dto.getDesignationIds());

        if (designations.size() != dto.getDesignationIds().size()) {
            throw new RuntimeException("One or more designations not found.");
        }

        List<FareRateCard> toSave = new ArrayList<>();

        for (Designation designation : designations) {
            for (FareRateCardRowDTO row : dto.getRows()) {
                validateRow(row);
                FareRateCard card = new FareRateCard();
                card.setDesignation(designation);
                mapRowToCard(row, card);
                toSave.add(card);
            }
        }

        List<FareRateCard> saved = fareRateCardRepository.saveAll(toSave);
        return saved.stream().map(this::toResponseDTO).toList();
    }

    // returns all FRC records, optionally filtered by designation IDs.
    // when designationIds is null or empty, returns everything.
    public List<FareRateCardResponseDTO> getFareRateCards(List<Long> designationIds) {
        List<FareRateCard> cards;

        if (designationIds != null && !designationIds.isEmpty()) {
            cards = fareRateCardRepository.findByDesignationIdIn(designationIds);
        } else {
            cards = fareRateCardRepository.findAll();
        }

        return cards.stream().map(this::toResponseDTO).toList();
    }

    // updates a single FRC record by ID.
    // designation is not changeable on edit — only the rule fields.
    @Transactional
    public FareRateCardResponseDTO updateFareRateCard(Long id, FareRateCardUpdateDTO dto) {
        FareRateCard card = fareRateCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fare rate card not found with id: " + id));

        card.setFromDistance(dto.getFromDistance());
        card.setToDistance(dto.getToDistance());
        card.setAllowanceType(dto.getAllowanceType());
        card.setApplicableTo(dto.getApplicableTo());
        card.setFare(dto.getFare());
        card.setFrcCode(dto.getFrcCode());
        card.setDescription(dto.getDescription());

        return toResponseDTO(fareRateCardRepository.save(card));
    }

    // hard deletes a single FRC record by ID
    @Transactional
    public void deleteFareRateCard(Long id) {
        if (!fareRateCardRepository.existsById(id)) {
            throw new RuntimeException("Fare rate card not found with id: " + id);
        }
        fareRateCardRepository.deleteById(id);
    }

    // maps a row DTO fields onto an existing or new FareRateCard entity
    private void mapRowToCard(FareRateCardRowDTO row, FareRateCard card) {
        card.setFromDistance(row.getFromDistance());
        card.setToDistance(row.getToDistance());
        card.setAllowanceType(row.getAllowanceType());
        card.setApplicableTo(row.getApplicableTo());
        card.setFare(row.getFare());
        card.setFrcCode(row.getFrcCode());
        card.setDescription(row.getDescription());
    }

    // basic validation for required fields in a row before saving
    private void validateRow(FareRateCardRowDTO row) {
        if (row.getFromDistance() == null || row.getToDistance() == null) {
            throw new IllegalArgumentException("From Distance and To Distance are required.");
        }
        if (row.getAllowanceType() == null || row.getAllowanceType().isBlank()) {
            throw new IllegalArgumentException("Allowance type is required.");
        }
        if (row.getApplicableTo() == null || row.getApplicableTo().isBlank()) {
            throw new IllegalArgumentException("Applicable To is required.");
        }
        if (row.getFare() == null) {
            throw new IllegalArgumentException("Fare (TA per KM) is required.");
        }
    }

    // converts a FareRateCard entity to the flat DTO the table expects
    private FareRateCardResponseDTO toResponseDTO(FareRateCard card) {
        return new FareRateCardResponseDTO(
                card.getId(),
                card.getDesignation().getId(),
                card.getDesignation().getName(),
                card.getDesignation().getName(),   // "Name" column same as designation name
                card.getFromDistance(),
                card.getToDistance(),
                card.getAllowanceType(),
                card.getApplicableTo(),
                card.getFare(),
                card.getFrcCode(),
                card.getDescription()
        );
    }
}