package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.FareRateCardCreateDTO;
import com.mrreporting.backend.dto.FareRateCardResponseDTO;
import com.mrreporting.backend.dto.FareRateCardRowDTO;
import com.mrreporting.backend.dto.FareRateCardUpdateDTO;
import com.mrreporting.backend.entity.Designation;
import com.mrreporting.backend.entity.FareRateCard;
import com.mrreporting.backend.repository.DesignationRepository;
import com.mrreporting.backend.repository.FareRateCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareRateCardServiceTest {

    @InjectMocks
    private FareRateCardService fareRateCardService;

    @Mock
    private FareRateCardRepository fareRateCardRepository;

    @Mock
    private DesignationRepository designationRepository;

    private Designation testDesignation;
    private FareRateCard testFareRateCard;
    private FareRateCardRowDTO testRow;

    @BeforeEach
    void setUp() {
        testDesignation = new Designation();
        testDesignation.setId(1L);
        testDesignation.setName("MR");
        testDesignation.setLevel(1);

        testRow = new FareRateCardRowDTO();
        testRow.setFromDistance(new BigDecimal("10"));
        testRow.setToDistance(new BigDecimal("50"));
        testRow.setAllowanceType("KM Wise");
        testRow.setApplicableTo("hq");
        testRow.setFare(new BigDecimal("1.5"));
        testRow.setFrcCode("mr-hq");
        testRow.setDescription("MR HQ FRC");

        testFareRateCard = new FareRateCard();
        testFareRateCard.setId(1L);
        testFareRateCard.setDesignation(testDesignation);
        testFareRateCard.setFromDistance(new BigDecimal("10"));
        testFareRateCard.setToDistance(new BigDecimal("50"));
        testFareRateCard.setAllowanceType("KM Wise");
        testFareRateCard.setApplicableTo("hq");
        testFareRateCard.setFare(new BigDecimal("1.5"));
        testFareRateCard.setFrcCode("mr-hq");
        testFareRateCard.setDescription("MR HQ FRC");
    }

    // create tests

    @Test
    void createFareRateCards_throwsException_whenDesignationIdsIsEmpty() {
        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of());
        dto.setRows(List.of(testRow));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertEquals("At least one designation must be selected.", ex.getMessage());
    }

    @Test
    void createFareRateCards_throwsException_whenRowsIsEmpty() {
        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertEquals("At least one rule row must be provided.", ex.getMessage());
    }

    @Test
    void createFareRateCards_throwsException_whenDesignationIdsIsNull() {
        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(null);
        dto.setRows(List.of(testRow));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertEquals("At least one designation must be selected.", ex.getMessage());
    }

    @Test
    void createFareRateCards_throwsException_whenDesignationNotFoundInDb() {
        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of(testRow));

        // db returns empty list — designation not found
        when(designationRepository.findAllById(anyList())).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertTrue(ex.getMessage().contains("designations not found"));
    }

    @Test
    void createFareRateCards_throwsException_whenFromDistanceIsNull() {
        testRow.setFromDistance(null);

        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of(testRow));

        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertTrue(ex.getMessage().contains("From Distance and To Distance are required"));
    }

    @Test
    void createFareRateCards_throwsException_whenAllowanceTypeIsBlank() {
        testRow.setAllowanceType("");

        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of(testRow));

        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertTrue(ex.getMessage().contains("Allowance type is required"));
    }

    @Test
    void createFareRateCards_throwsException_whenApplicableToIsBlank() {
        testRow.setApplicableTo("");

        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of(testRow));

        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertTrue(ex.getMessage().contains("Applicable To is required"));
    }

    @Test
    void createFareRateCards_throwsException_whenFareIsNull() {
        testRow.setFare(null);

        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of(testRow));

        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fareRateCardService.createFareRateCards(dto));

        assertTrue(ex.getMessage().contains("Fare (TA per KM) is required"));
    }

    @Test
    void createFareRateCards_savesOneRecord_forOneDesignationAndOneRow() {
        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of(testRow));

        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));
        when(fareRateCardRepository.saveAll(anyList())).thenReturn(List.of(testFareRateCard));

        List<FareRateCardResponseDTO> result = fareRateCardService.createFareRateCards(dto);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fareRateCardRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createFareRateCards_crossProduct_savesSixRecords_forTwoDesignationsAndThreeRows() {
        Designation designation2 = new Designation();
        designation2.setId(2L);
        designation2.setName("ASM");

        FareRateCardRowDTO row2 = buildRow("50", "150", "KM Wise", "ex", "2", "mr-ex");
        FareRateCardRowDTO row3 = buildRow("150", "500", "Lumsum", "out", "3", "mr-out");

        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L, 2L));
        dto.setRows(List.of(testRow, row2, row3));

        when(designationRepository.findAllById(anyList()))
                .thenReturn(List.of(testDesignation, designation2));
        when(fareRateCardRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        fareRateCardService.createFareRateCards(dto);

        // 2 designations x 3 rows = 6 records
        verify(fareRateCardRepository).saveAll(argThat(list ->
                ((List<?>) list).size() == 6
        ));
    }

    @Test
    void createFareRateCards_mapsAllFieldsCorrectly() {
        FareRateCardCreateDTO dto = new FareRateCardCreateDTO();
        dto.setDesignationIds(List.of(1L));
        dto.setRows(List.of(testRow));

        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));
        when(fareRateCardRepository.saveAll(anyList())).thenReturn(List.of(testFareRateCard));

        List<FareRateCardResponseDTO> result = fareRateCardService.createFareRateCards(dto);

        FareRateCardResponseDTO dto1 = result.get(0);
        assertEquals("MR", dto1.getDesignationName());
        assertEquals(new BigDecimal("10"), dto1.getFromDistance());
        assertEquals(new BigDecimal("50"), dto1.getToDistance());
        assertEquals("KM Wise", dto1.getAllowanceType());
        assertEquals("hq", dto1.getApplicableTo());
        assertEquals(new BigDecimal("1.5"), dto1.getFare());
        assertEquals("mr-hq", dto1.getFrcCode());
        assertEquals("MR HQ FRC", dto1.getDescription());
    }

    // getFareRateCards tests

    @Test
    void getFareRateCards_returnsAllRecords_whenDesignationIdsIsNull() {
        when(fareRateCardRepository.findAll()).thenReturn(List.of(testFareRateCard));

        List<FareRateCardResponseDTO> result = fareRateCardService.getFareRateCards(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fareRateCardRepository, times(1)).findAll();
        verify(fareRateCardRepository, never()).findByDesignationIdIn(anyList());
    }

    @Test
    void getFareRateCards_returnsFilteredRecords_whenDesignationIdsProvided() {
        when(fareRateCardRepository.findByDesignationIdIn(List.of(1L)))
                .thenReturn(List.of(testFareRateCard));

        List<FareRateCardResponseDTO> result = fareRateCardService.getFareRateCards(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fareRateCardRepository, times(1)).findByDesignationIdIn(List.of(1L));
        verify(fareRateCardRepository, never()).findAll();
    }

    @Test
    void getFareRateCards_returnsEmptyList_whenNoRecordsExist() {
        when(fareRateCardRepository.findAll()).thenReturn(List.of());

        List<FareRateCardResponseDTO> result = fareRateCardService.getFareRateCards(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // update tests

    @Test
    void updateFareRateCard_throwsException_whenIdNotFound() {
        when(fareRateCardRepository.findById(99L)).thenReturn(Optional.empty());

        FareRateCardUpdateDTO dto = buildUpdateDTO();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fareRateCardService.updateFareRateCard(99L, dto));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void updateFareRateCard_updatesFieldsAndSaves() {
        when(fareRateCardRepository.findById(1L)).thenReturn(Optional.of(testFareRateCard));
        when(fareRateCardRepository.save(any())).thenReturn(testFareRateCard);

        FareRateCardUpdateDTO dto = buildUpdateDTO();

        FareRateCardResponseDTO result = fareRateCardService.updateFareRateCard(1L, dto);

        assertNotNull(result);
        verify(fareRateCardRepository, times(1)).save(any(FareRateCard.class));
    }

    @Test
    void updateFareRateCard_doesNotChangeDesignation() {
        when(fareRateCardRepository.findById(1L)).thenReturn(Optional.of(testFareRateCard));
        when(fareRateCardRepository.save(any())).thenReturn(testFareRateCard);

        FareRateCardUpdateDTO dto = buildUpdateDTO();
        fareRateCardService.updateFareRateCard(1L, dto);

        // designation on the saved entity should remain MR — update cannot change it
        assertEquals("MR", testFareRateCard.getDesignation().getName());
    }

    // delete tests

    @Test
    void deleteFareRateCard_throwsException_whenIdNotFound() {
        when(fareRateCardRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fareRateCardService.deleteFareRateCard(99L));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void deleteFareRateCard_deletesRecord_whenIdExists() {
        when(fareRateCardRepository.existsById(1L)).thenReturn(true);

        fareRateCardService.deleteFareRateCard(1L);

        verify(fareRateCardRepository, times(1)).deleteById(1L);
    }

    // helpers

    private FareRateCardRowDTO buildRow(
            String from, String to, String allowance,
            String applicable, String fare, String code) {

        FareRateCardRowDTO row = new FareRateCardRowDTO();
        row.setFromDistance(new BigDecimal(from));
        row.setToDistance(new BigDecimal(to));
        row.setAllowanceType(allowance);
        row.setApplicableTo(applicable);
        row.setFare(new BigDecimal(fare));
        row.setFrcCode(code);
        return row;
    }

    private FareRateCardUpdateDTO buildUpdateDTO() {
        FareRateCardUpdateDTO dto = new FareRateCardUpdateDTO();
        dto.setFromDistance(new BigDecimal("20"));
        dto.setToDistance(new BigDecimal("60"));
        dto.setAllowanceType("Lumsum");
        dto.setApplicableTo("ex");
        dto.setFare(new BigDecimal("2.5"));
        dto.setFrcCode("mr-ex-updated");
        dto.setDescription("Updated description");
        return dto;
    }
}