package com.mrreporting.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MrAreaRequestDTO {
    private String areaName;
    private String areaCode;
    @JsonAlias({"area_type", "type", "selectedType"})
    private String areaType;

    @JsonSetter("areaType")
    public void setAreaTypeFromObject(Object areaType) {
        assignAreaType(areaType);
    }

    @JsonSetter("type")
    public void setTypeFromObject(Object type) {
        assignAreaType(type);
    }

    @JsonSetter("selectedType")
    public void setSelectedTypeFromObject(Object type) {
        assignAreaType(type);
    }

    private void assignAreaType(Object value) {
        String resolvedType = extractString(value);
        if (resolvedType != null && !resolvedType.isBlank()) {
            this.areaType = resolvedType.trim();
        }
    }

    private String extractString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String text) {
            return text;
        }
        if (value instanceof Map<?, ?> map) {
            for (String key : List.of("value", "label", "areaType", "type", "id")) {
                Object nested = map.get(key);
                if (nested instanceof String nestedText && !nestedText.isBlank()) {
                    return nestedText;
                }
            }
        }
        return null;
    }
}
