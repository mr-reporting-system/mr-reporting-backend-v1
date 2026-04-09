package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.CrmDoctorResponseDTO;
import com.mrreporting.backend.entity.Area;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmDoctorServiceTest {

    @InjectMocks
    private CrmDoctorService crmDoctorService;

    @Mock
    private DoctorRepository doctorRepository;

    private Doctor testDoctor;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setName("Ashish");
        testEmployee.setIsActive(true);

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setDoctorName("Dr. Sharma");
        testDoctor.setDoctorCode("DOC-001");
        testDoctor.setSpecialization("Cardiologist");
        testDoctor.setCategory("A");
        testDoctor.setPhone("9898989898");
        testDoctor.setCrmStatus("Not Linked");
        testDoctor.setSponsorshipStatus("Not Submitted");
        testDoctor.setIsActive(true);
        testDoctor.setEmployee(testEmployee);
    }

    // getDoctorsByCrmStatus tests

    @Test
    void getDoctorsByCrmStatus_throwsException_whenInvalidStatusPassed() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> crmDoctorService.getDoctorsByCrmStatus(1L, "INVALID_STATUS"));

        assertTrue(ex.getMessage().contains("Invalid crmStatus value"));
    }

    @Test
    void getDoctorsByCrmStatus_returnsNotLinkedDoctors_whenStatusIsNotLinked() {
        when(doctorRepository.findByEmployeeIdAndIsActiveTrueAndCrmStatus(1L, "Not Linked"))
                .thenReturn(List.of(testDoctor));

        List<CrmDoctorResponseDTO> result =
                crmDoctorService.getDoctorsByCrmStatus(1L, "Not Linked");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Sharma", result.get(0).getDoctorName());
        assertEquals("Not Linked", result.get(0).getCrmStatus());
    }

    @Test
    void getDoctorsByCrmStatus_returnsLinkedDoctors_whenStatusIsLinked() {
        testDoctor.setCrmStatus("Linked");

        when(doctorRepository.findByEmployeeIdAndIsActiveTrueAndCrmStatus(1L, "Linked"))
                .thenReturn(List.of(testDoctor));

        List<CrmDoctorResponseDTO> result =
                crmDoctorService.getDoctorsByCrmStatus(1L, "Linked");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Linked", result.get(0).getCrmStatus());
    }

    @Test
    void getDoctorsByCrmStatus_returnsEmptyList_whenNoDoctorsFound() {
        when(doctorRepository.findByEmployeeIdAndIsActiveTrueAndCrmStatus(1L, "Not Linked"))
                .thenReturn(List.of());

        List<CrmDoctorResponseDTO> result =
                crmDoctorService.getDoctorsByCrmStatus(1L, "Not Linked");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDoctorsByCrmStatus_mapsAllFieldsCorrectly() {
        when(doctorRepository.findByEmployeeIdAndIsActiveTrueAndCrmStatus(1L, "Not Linked"))
                .thenReturn(List.of(testDoctor));

        List<CrmDoctorResponseDTO> result =
                crmDoctorService.getDoctorsByCrmStatus(1L, "Not Linked");

        CrmDoctorResponseDTO dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Dr. Sharma", dto.getDoctorName());
        assertEquals("DOC-001", dto.getDoctorCode());
        assertEquals("Cardiologist", dto.getSpecialization());
        assertEquals("A", dto.getCategory());
        assertEquals("9898989898", dto.getPhone());
        assertEquals("Not Submitted", dto.getSponsorshipStatus());
    }

    // getLinkedDoctorCount tests

    @Test
    void getLinkedDoctorCount_returnsCorrectCount() {
        when(doctorRepository.countByEmployeeIdAndIsActiveTrueAndCrmStatus(1L, "Linked"))
                .thenReturn(3L);

        long count = crmDoctorService.getLinkedDoctorCount(1L);

        assertEquals(3L, count);
    }

    @Test
    void getLinkedDoctorCount_returnsZero_whenNoLinkedDoctors() {
        when(doctorRepository.countByEmployeeIdAndIsActiveTrueAndCrmStatus(1L, "Linked"))
                .thenReturn(0L);

        long count = crmDoctorService.getLinkedDoctorCount(1L);

        assertEquals(0L, count);
    }

    // linkDoctors tests

    @Test
    void linkDoctors_throwsException_whenDoctorIdsIsEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> crmDoctorService.linkDoctors(1L, List.of()));

        assertTrue(ex.getMessage().contains("No doctor IDs provided"));
    }

    @Test
    void linkDoctors_throwsException_whenDoctorIdsIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> crmDoctorService.linkDoctors(1L, null));

        assertTrue(ex.getMessage().contains("No doctor IDs provided"));
    }

    @Test
    void linkDoctors_throwsException_whenNoDoctorsBelongToEmployee() {
        // doctor belongs to a different employee (id = 99)
        Employee otherEmployee = new Employee();
        otherEmployee.setId(99L);
        testDoctor.setEmployee(otherEmployee);

        when(doctorRepository.findAllById(List.of(1L))).thenReturn(List.of(testDoctor));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> crmDoctorService.linkDoctors(1L, List.of(1L)));

        assertTrue(ex.getMessage().contains("No valid active doctors found"));
    }

    @Test
    void linkDoctors_throwsException_whenDoctorIsInactive() {
        testDoctor.setIsActive(false);

        when(doctorRepository.findAllById(List.of(1L))).thenReturn(List.of(testDoctor));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> crmDoctorService.linkDoctors(1L, List.of(1L)));

        assertTrue(ex.getMessage().contains("No valid active doctors found"));
    }

    @Test
    void linkDoctors_setsCrmStatusToLinked_andReturnsCount() {
        when(doctorRepository.findAllById(List.of(1L))).thenReturn(List.of(testDoctor));
        when(doctorRepository.saveAll(anyList())).thenReturn(List.of(testDoctor));

        int count = crmDoctorService.linkDoctors(1L, List.of(1L));

        assertEquals(1, count);
        assertEquals("Linked", testDoctor.getCrmStatus());
        verify(doctorRepository, times(1)).saveAll(anyList());
    }

    @Test
    void linkDoctors_linksMultipleDoctors_andReturnsCorrectCount() {
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setIsActive(true);
        doctor2.setEmployee(testEmployee);
        doctor2.setCrmStatus("Not Linked");

        when(doctorRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(testDoctor, doctor2));
        when(doctorRepository.saveAll(anyList())).thenReturn(List.of(testDoctor, doctor2));

        int count = crmDoctorService.linkDoctors(1L, List.of(1L, 2L));

        assertEquals(2, count);
        assertEquals("Linked", testDoctor.getCrmStatus());
        assertEquals("Linked", doctor2.getCrmStatus());
    }

    // unlinkDoctors tests

    @Test
    void unlinkDoctors_throwsException_whenDoctorIdsIsEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> crmDoctorService.unlinkDoctors(1L, List.of()));

        assertTrue(ex.getMessage().contains("No doctor IDs provided"));
    }

    @Test
    void unlinkDoctors_setsCrmStatusToNotLinked_andReturnsCount() {
        testDoctor.setCrmStatus("Linked");

        when(doctorRepository.findAllById(List.of(1L))).thenReturn(List.of(testDoctor));
        when(doctorRepository.saveAll(anyList())).thenReturn(List.of(testDoctor));

        int count = crmDoctorService.unlinkDoctors(1L, List.of(1L));

        assertEquals(1, count);
        assertEquals("Not Linked", testDoctor.getCrmStatus());
        verify(doctorRepository, times(1)).saveAll(anyList());
    }

    @Test
    void unlinkDoctors_throwsException_whenNoDoctorsBelongToEmployee() {
        Employee otherEmployee = new Employee();
        otherEmployee.setId(99L);
        testDoctor.setEmployee(otherEmployee);
        testDoctor.setCrmStatus("Linked");

        when(doctorRepository.findAllById(List.of(1L))).thenReturn(List.of(testDoctor));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> crmDoctorService.unlinkDoctors(1L, List.of(1L)));

        assertTrue(ex.getMessage().contains("No valid active doctors found"));
    }
}