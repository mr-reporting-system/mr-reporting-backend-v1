package com.mrreporting.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "districts")
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "district_name", nullable = false, length = 100)
    @JsonProperty("district_name")
    private String districtName;

    // Here is our link back to the State table!
    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @Column(name = "is_active")
    private Boolean isActive = false;
}