package com.app.onboarding.repository;



import com.app.onboarding.model.EmployeeEntity;
import com.app.onboarding.model.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    // Fetch employees that are not deleted
    List<EmployeeEntity> findByIsDeletedFalse();

    // Fetch employees based on status and excluding deleted ones
    List<EmployeeEntity> findByStatusAndIsDeletedFalse(String status);

    List<EmployeeEntity> findByStatus(EmployeeStatus status);

    // Fetch employees that are deleted
    List<EmployeeEntity> findByIsDeletedTrue();
}




