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
class DailyAllowanceServiceTest {

    @InjectMocks
    private DailyAllowanceService dailyAllowanceService;

    @Mock
    private DailyAllowanceRepository dailyAllowanceRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private DesignationRepository designationRepository;

    private State testState;
    private Designation testDesignation;
    private DailyAllowance testDailyAllowance;

    @BeforeEach
    void setUp() {
        testState = new State();
        testState.setId(1);
        testState.setStateName("Uttarakhand");
        testState.setIsActive(true);

        testDesignation = new Designation();
        testDesignation.setId(1L);
        testDesignation.setName("MR");
        testDesignation.setLevel(1);

        testDailyAllowance = new DailyAllowance();
        testDailyAllowance.setId(1L);
        testDailyAllowance.setState(testState);
        testDailyAllowance.setDesignation(testDesignation);
        testDailyAllowance.setHqDa(new BigDecimal("150"));
        testDailyAllowance.setExDa(new BigDecimal("200"));
        testDailyAllowance.setOutDa(new BigDecimal("400"));
        testDailyAllowance.setMobileAllowance(new BigDecimal("250"));
        testDailyAllowance.setNetAllowance(new BigDecimal("100"));
        testDailyAllowance.setPostageStationary(new BigDecimal("50"));
        testDailyAllowance.setPostageFreight(new BigDecimal("30"));
    }

    // create tests

    @Test
    void createDailyAllowances_throwsException_whenStateIdsIsEmpty() {
        DailyAllowanceCreateDTO dto = new DailyAllowanceCreateDTO();
        dto.setStateIds(List.of());
        dto.setDesignationIds(List.of(1L));
        dto.setHqDa(new BigDecimal("150"));
        dto.setExDa(new BigDecimal("200"));
        dto.setOutDa(new BigDecimal("400"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dailyAllowanceService.createDailyAllowances(dto));

        assertEquals("At least one state must be selected.", ex.getMessage());
    }

    @Test
    void createDailyAllowances_throwsException_whenDesignationIdsIsEmpty() {
        DailyAllowanceCreateDTO dto = new DailyAllowanceCreateDTO();
        dto.setStateIds(List.of(1));
        dto.setDesignationIds(List.of());
        dto.setHqDa(new BigDecimal("150"));
        dto.setExDa(new BigDecimal("200"));
        dto.setOutDa(new BigDecimal("400"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dailyAllowanceService.createDailyAllowances(dto));

        assertEquals("At least one designation must be selected.", ex.getMessage());
    }

    @Test
    void createDailyAllowances_throwsException_whenHqDaIsNull() {
        DailyAllowanceCreateDTO dto = new DailyAllowanceCreateDTO();
        dto.setStateIds(List.of(1));
        dto.setDesignationIds(List.of(1L));
        dto.setHqDa(null);
        dto.setExDa(new BigDecimal("200"));
        dto.setOutDa(new BigDecimal("400"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dailyAllowanceService.createDailyAllowances(dto));

        assertEquals("HQ DA, EX DA, and OUT DA are required.", ex.getMessage());
    }

    @Test
    void createDailyAllowances_throwsException_whenStateNotFoundInDb() {
        DailyAllowanceCreateDTO dto = buildValidCreateDTO();

        when(stateRepository.findAllById(anyList())).thenReturn(List.of());
        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dailyAllowanceService.createDailyAllowances(dto));

        assertTrue(ex.getMessage().contains("states not found"));
    }

    @Test
    void createDailyAllowances_throwsException_whenDesignationNotFoundInDb() {
        DailyAllowanceCreateDTO dto = buildValidCreateDTO();

        when(stateRepository.findAllById(anyList())).thenReturn(List.of(testState));
        when(designationRepository.findAllById(anyList())).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dailyAllowanceService.createDailyAllowances(dto));

        assertTrue(ex.getMessage().contains("designations not found"));
    }

    @Test
    void createDailyAllowances_skipsExistingCombination_andSavesNew() {
        DailyAllowanceCreateDTO dto = buildValidCreateDTO();

        when(stateRepository.findAllById(anyList())).thenReturn(List.of(testState));
        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));
        when(dailyAllowanceRepository.findByStateIdAndDesignationId(1, 1L))
                .thenReturn(Optional.empty());
        when(dailyAllowanceRepository.saveAll(anyList()))
                .thenReturn(List.of(testDailyAllowance));

        List<DailyAllowanceResponseDTO> result =
                dailyAllowanceService.createDailyAllowances(dto);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dailyAllowanceRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createDailyAllowances_throwsException_whenAllCombinationsAlreadyExist() {
        DailyAllowanceCreateDTO dto = buildValidCreateDTO();

        when(stateRepository.findAllById(anyList())).thenReturn(List.of(testState));
        when(designationRepository.findAllById(anyList())).thenReturn(List.of(testDesignation));
        when(dailyAllowanceRepository.findByStateIdAndDesignationId(1, 1L))
                .thenReturn(Optional.of(testDailyAllowance));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dailyAllowanceService.createDailyAllowances(dto));

        assertTrue(ex.getMessage().contains("already have a Daily Allowance rule"));
    }

    @Test
    void createDailyAllowances_crossProduct_createsFourRecords_forTwoStatesAndTwoDesignations() {
        State state2 = new State();
        state2.setId(2);
        state2.setStateName("Uttar Pradesh");

        Designation designation2 = new Designation();
        designation2.setId(2L);
        designation2.setName("ASM");

        DailyAllowanceCreateDTO dto = new DailyAllowanceCreateDTO();
        dto.setStateIds(List.of(1, 2));
        dto.setDesignationIds(List.of(1L, 2L));
        dto.setHqDa(new BigDecimal("150"));
        dto.setExDa(new BigDecimal("200"));
        dto.setOutDa(new BigDecimal("400"));

        when(stateRepository.findAllById(anyList())).thenReturn(List.of(testState, state2));
        when(designationRepository.findAllById(anyList()))
                .thenReturn(List.of(testDesignation, designation2));
        when(dailyAllowanceRepository.findByStateIdAndDesignationId(any(), any()))
                .thenReturn(Optional.empty());
        when(dailyAllowanceRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        dailyAllowanceService.createDailyAllowances(dto);

        // verify saveAll was called with 4 records (2 states x 2 designations)
        verify(dailyAllowanceRepository).saveAll(argThat(list ->
                ((List<?>) list).size() == 4
        ));
    }

    // get tests

    @Test
    void getAllDailyAllowances_returnsAllRecords() {
        when(dailyAllowanceRepository.findAll()).thenReturn(List.of(testDailyAllowance));

        List<DailyAllowanceResponseDTO> result = dailyAllowanceService.getAllDailyAllowances();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Uttarakhand", result.get(0).getStateName());
        assertEquals("MR", result.get(0).getDesignationName());
    }

    @Test
    void getAllDailyAllowances_calculatesSpecialAllowanceCorrectly() {
        when(dailyAllowanceRepository.findAll()).thenReturn(List.of(testDailyAllowance));

        List<DailyAllowanceResponseDTO> result = dailyAllowanceService.getAllDailyAllowances();

        // special allowance = postageStationary (50) + postageFreight (30) = 80
        assertEquals(new BigDecimal("80"), result.get(0).getSpecialAllowance());
    }

    @Test
    void getAllDailyAllowances_returnsEmptyList_whenNoRecordsExist() {
        when(dailyAllowanceRepository.findAll()).thenReturn(List.of());

        List<DailyAllowanceResponseDTO> result = dailyAllowanceService.getAllDailyAllowances();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // update tests

    @Test
    void updateDailyAllowance_throwsException_whenIdNotFound() {
        when(dailyAllowanceRepository.findById(99L)).thenReturn(Optional.empty());

        DailyAllowanceUpdateDTO dto = new DailyAllowanceUpdateDTO();
        dto.setHqDa(new BigDecimal("200"));
        dto.setExDa(new BigDecimal("300"));
        dto.setOutDa(new BigDecimal("500"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dailyAllowanceService.updateDailyAllowance(99L, dto));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void updateDailyAllowance_updatesFieldsCorrectly() {
        when(dailyAllowanceRepository.findById(1L)).thenReturn(Optional.of(testDailyAllowance));
        when(dailyAllowanceRepository.save(any())).thenReturn(testDailyAllowance);

        DailyAllowanceUpdateDTO dto = new DailyAllowanceUpdateDTO();
        dto.setHqDa(new BigDecimal("300"));
        dto.setExDa(new BigDecimal("400"));
        dto.setOutDa(new BigDecimal("600"));
        dto.setMobileAllowance(new BigDecimal("500"));
        dto.setNetAllowance(new BigDecimal("200"));
        dto.setPostageStationary(new BigDecimal("60"));
        dto.setPostageFreight(new BigDecimal("40"));

        DailyAllowanceResponseDTO result =
                dailyAllowanceService.updateDailyAllowance(1L, dto);

        assertNotNull(result);
        verify(dailyAllowanceRepository, times(1)).save(any(DailyAllowance.class));
    }

    // delete tests

    @Test
    void deleteDailyAllowance_throwsException_whenIdNotFound() {
        when(dailyAllowanceRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dailyAllowanceService.deleteDailyAllowance(99L));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void deleteDailyAllowance_deletesRecord_whenIdExists() {
        when(dailyAllowanceRepository.existsById(1L)).thenReturn(true);

        dailyAllowanceService.deleteDailyAllowance(1L);

        verify(dailyAllowanceRepository, times(1)).deleteById(1L);
    }

    // helper method to avoid repeating DTO setup in every test
    private DailyAllowanceCreateDTO buildValidCreateDTO() {
        DailyAllowanceCreateDTO dto = new DailyAllowanceCreateDTO();
        dto.setStateIds(List.of(1));
        dto.setDesignationIds(List.of(1L));
        dto.setHqDa(new BigDecimal("150"));
        dto.setExDa(new BigDecimal("200"));
        dto.setOutDa(new BigDecimal("400"));
        dto.setMobileAllowance(new BigDecimal("250"));
        dto.setNetAllowance(new BigDecimal("100"));
        dto.setPostageStationary(new BigDecimal("50"));
        dto.setPostageFreight(new BigDecimal("30"));
        return dto;
    }
}