package com.mrreporting.backend.service;

import com.mrreporting.backend.dto.MrTourProgramDayRowDTO;
import com.mrreporting.backend.dto.MrTourProgramMonthDetailDTO;
import com.mrreporting.backend.dto.MrTourProgramSubmitRequestDTO;
import com.mrreporting.backend.dto.MrTourProgramSubmitResponseDTO;
import com.mrreporting.backend.entity.Doctor;
import com.mrreporting.backend.entity.Employee;
import com.mrreporting.backend.entity.Provider;
import com.mrreporting.backend.entity.TourProgram;
import com.mrreporting.backend.entity.TourProgramDay;
import com.mrreporting.backend.repository.TourProgramDayRepository;
import com.mrreporting.backend.repository.TourProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class MrTourProgramService {

    @Autowired
    private MrContextService mrContextService;

    @Autowired
    private TourProgramRepository tourProgramRepository;

    @Autowired
    private TourProgramDayRepository tourProgramDayRepository;

    @Transactional
    public MrTourProgramSubmitResponseDTO submitTourProgram(MrTourProgramSubmitRequestDTO dto) {
        validateSubmitRequest(dto);

        Employee employee = mrContextService.getCurrentMr();
        List<LocalDate> dates = uniqueDates(dto.getDates());

        LocalDate firstDate = dates.get(0);
        int month = firstDate.getMonthValue();
        int year = firstDate.getYear();

        validateSingleMonth(dates, month, year);

        TourProgram tourProgram = tourProgramRepository.findByEmployeeIdAndMonthAndYear(employee.getId(), month, year)
                .orElseGet(() -> createTourProgram(employee, month, year));

        if (Boolean.TRUE.equals(tourProgram.getIsApproved())) {
            throw new RuntimeException("Approved tour program cannot be modified.");
        }

        long updatedCount = 0L;
        for (LocalDate date : dates) {
            TourProgramDay day = tourProgramDayRepository.findByTourProgramIdAndDate(tourProgram.getId(), date)
                    .orElseGet(() -> createTourProgramDay(tourProgram, date));

            day.setActivityType(clean(dto.getActivityType()));
            day.setRemark(cleanNullable(dto.getRemarks()));

            if (day.getId() == null) {
                tourProgram.getDays().add(day);
            }

            updatedCount++;
        }

        tourProgram.setIsSubmitted(true);
        tourProgram.setSubmittedAt(LocalDateTime.now());
        tourProgram.setIsApproved(false);
        tourProgram.setApprovedAt(null);
        tourProgram.setApprovedByName(null);
        tourProgram.setRejectionMessage(null);

        TourProgram savedProgram = tourProgramRepository.save(tourProgram);

        return new MrTourProgramSubmitResponseDTO(
                savedProgram.getId(),
                savedProgram.getMonth(),
                savedProgram.getYear(),
                updatedCount,
                savedProgram.getIsSubmitted(),
                savedProgram.getIsApproved()
        );
    }

    @Transactional(readOnly = true)
    public MrTourProgramMonthDetailDTO getMonthDetail(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new RuntimeException("month must be between 1 and 12.");
        }
        if (year == null || year < 2000 || year > 2100) {
            throw new RuntimeException("year is invalid.");
        }

        Employee employee = mrContextService.getCurrentMr();
        TourProgram tourProgram = tourProgramRepository.findByEmployeeIdAndMonthAndYear(employee.getId(), month, year)
                .orElse(null);

        if (tourProgram == null) {
            return new MrTourProgramMonthDetailDTO(
                    null,
                    month,
                    year,
                    false,
                    null,
                    false,
                    null,
                    null,
                    null,
                    List.of()
            );
        }

        List<TourProgramDay> days = tourProgramDayRepository.findByTourProgramIdOrderByDate(tourProgram.getId());
        List<MrTourProgramDayRowDTO> rows = days.stream()
                .map(day -> toRow(day, Boolean.TRUE.equals(tourProgram.getIsApproved())))
                .toList();

        return new MrTourProgramMonthDetailDTO(
                tourProgram.getId(),
                tourProgram.getMonth(),
                tourProgram.getYear(),
                tourProgram.getIsSubmitted(),
                tourProgram.getSubmittedAt(),
                tourProgram.getIsApproved(),
                tourProgram.getApprovedAt(),
                tourProgram.getApprovedByName(),
                tourProgram.getRejectionMessage(),
                rows
        );
    }

    private TourProgram createTourProgram(Employee employee, Integer month, Integer year) {
        TourProgram tourProgram = new TourProgram();
        tourProgram.setEmployee(employee);
        tourProgram.setMonth(month);
        tourProgram.setYear(year);
        tourProgram.setIsSubmitted(false);
        tourProgram.setIsApproved(false);
        return tourProgram;
    }

    private TourProgramDay createTourProgramDay(TourProgram tourProgram, LocalDate date) {
        TourProgramDay day = new TourProgramDay();
        day.setTourProgram(tourProgram);
        day.setDate(date);
        return day;
    }

    private MrTourProgramDayRowDTO toRow(TourProgramDay day, boolean approved) {
        List<String> submittedDoctors = extractDoctorNames(day);
        List<String> submittedChemists = extractChemistNames(day);

        return new MrTourProgramDayRowDTO(
                day.getId(),
                day.getDate(),
                day.getActivityType(),
                approved ? day.getActivityType() : null,
                submittedDoctors,
                approved ? submittedDoctors : List.of(),
                submittedChemists,
                approved ? submittedChemists : List.of(),
                day.getRemark()
        );
    }

    private List<String> extractDoctorNames(TourProgramDay day) {
        List<String> names = new ArrayList<>();
        if (day.getDoctors() != null) {
            for (var item : day.getDoctors()) {
                Doctor doctor = item.getDoctor();
                if (doctor != null && doctor.getDoctorName() != null) {
                    names.add(doctor.getDoctorName());
                }
            }
        }
        return names;
    }

    private List<String> extractChemistNames(TourProgramDay day) {
        List<String> names = new ArrayList<>();
        if (day.getChemists() != null) {
            for (var item : day.getChemists()) {
                Provider provider = item.getProvider();
                if (provider != null && provider.getProviderName() != null) {
                    names.add(provider.getProviderName());
                }
            }
        }
        return names;
    }

    private void validateSubmitRequest(MrTourProgramSubmitRequestDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Tour program payload is required.");
        }
        if (dto.getDates() == null || dto.getDates().isEmpty()) {
            throw new RuntimeException("At least one date must be selected.");
        }
        if (isBlank(dto.getActivityType())) {
            throw new RuntimeException("activityType is required.");
        }
    }

    private void validateSingleMonth(List<LocalDate> dates, int month, int year) {
        boolean invalid = dates.stream().anyMatch(date -> date.getMonthValue() != month || date.getYear() != year);
        if (invalid) {
            throw new RuntimeException("All selected dates must belong to the same month and year.");
        }
    }

    private List<LocalDate> uniqueDates(List<LocalDate> dates) {
        return new ArrayList<>(new LinkedHashSet<>(dates));
    }

    private String clean(String value) {
        return value.trim();
    }

    private String cleanNullable(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
