package com.mrreporting.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DoctorDTO {
    // --- Core Relationships (IDs only) 🗺️ ---
    private Integer stateId;
    private Integer districtId;
    private Long employeeId;
    @JsonAlias({"area_id", "selectedAreaId"})
    private Long areaId;

    // --- Basic Doctor Info ✍️ ---
    private String doctorCode;
    private String doctorName;
    private String mslNo;
    private String category;
    private String degree;
    private String specialization;
    private String phone;
    private String gender;
    private String address;
    private String licenceNo;
    private String aadhaarNo;
    private String email;
    private Integer frequencyVisit;

    // --- Nested Lists (Additional Info) 👨‍👩‍👧‍👦 ---
    private List<DoctorChildDTO> children;
    private List<DoctorVisitLocationDTO> locations;

    @JsonSetter("area")
    public void setAreaFromObject(Object area) {
        assignAreaId(area);
    }

    @JsonSetter("selectedArea")
    public void setSelectedAreaFromObject(Object area) {
        assignAreaId(area);
    }

    private void assignAreaId(Object area) {
        Long resolvedAreaId = extractLong(area);
        if (resolvedAreaId != null) {
            this.areaId = resolvedAreaId;
        }
    }

    private Long extractLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text) {
            String normalized = text.trim();
            if (normalized.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(normalized);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (value instanceof Map<?, ?> map) {
            for (String key : List.of("id", "value", "areaId")) {
                Long nestedValue = extractLong(map.get(key));
                if (nestedValue != null) {
                    return nestedValue;
                }
            }
        }
        return null;
    }
}
