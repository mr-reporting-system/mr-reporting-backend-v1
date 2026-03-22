package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    List<Designation> findByNameIn(List<String> names);

    @Query("SELECT d FROM Designation d WHERE d.level < (SELECT MAX(d2.level) FROM Designation d2)")
    List<Designation> findDesignationsBelowTopLevel();
}