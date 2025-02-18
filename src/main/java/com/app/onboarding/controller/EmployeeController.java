package com.app.onboarding.controller;

import com.app.onboarding.dto.RegistrationLoginDto.EmployeeRequestDto;
import com.app.onboarding.model.EmployeeEntity;
import com.app.onboarding.model.EmployeeStatus;
import com.app.onboarding.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    // Create new employee
    @PostMapping
    public ResponseEntity<EmployeeEntity> onboardEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeEntity createdEmployee = employeeService.createEmployee(employeeRequestDto);
        return ResponseEntity.ok(createdEmployee);
    }

    // Edit existing employee
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeEntity> editEmployee(@PathVariable Long id, @RequestBody EmployeeEntity employeeDetails) {
        EmployeeEntity updatedEmployee = employeeService.editEmployee(id, employeeDetails);
        if (updatedEmployee != null) {
            return ResponseEntity.ok(updatedEmployee);
        }
        return ResponseEntity.notFound().build();
    }

    // Soft delete employee (mark as deleted)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteEmployee(@PathVariable Long id) {
        employeeService.softDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Verify employee (set status to VERIFIED)
    @PatchMapping("/{id}/verify")
    public ResponseEntity<EmployeeEntity> verifyEmployee(@PathVariable Long id) {
        EmployeeEntity verifiedEmployee = employeeService.verifyEmployee(id);
        if (verifiedEmployee != null) {
            return ResponseEntity.ok(verifiedEmployee);
        }
        return ResponseEntity.notFound().build();
    }

    // Fetch employees by status (Pending or Verified)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeEntity>> getEmployeesByStatus(@PathVariable String status) {
        try {
            // Convert string to enum
            EmployeeStatus employeeStatus = EmployeeStatus.valueOf(status); // Manually convert to enum

            List<EmployeeEntity> employees = employeeService.getEmployeesByStatus(employeeStatus);
            return ResponseEntity.ok(employees);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status: {}", status, e);
            return ResponseEntity.badRequest().body(null); // Return 400 if invalid status
        } catch (Exception e) {
            logger.error("Error in getEmployeesByStatus: ", e);
            return ResponseEntity.status(500).body(null); // General error handling
        }
    }
}



/*@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Create new employee
    @PostMapping
    public ResponseEntity<EmployeeEntity> onboardEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeEntity createdEmployee = employeeService.createEmployee(employeeRequestDto);
        return ResponseEntity.ok(createdEmployee);
    }

    // Edit existing employee
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeEntity> editEmployee(@PathVariable Long id, @RequestBody EmployeeEntity employeeDetails) {
        EmployeeEntity updatedEmployee = employeeService.editEmployee(id, employeeDetails);
        if (updatedEmployee != null) {
            return ResponseEntity.ok(updatedEmployee);
        }
        return ResponseEntity.notFound().build();
    }

    // Soft delete employee (mark as deleted)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteEmployee(@PathVariable Long id) {
        employeeService.softDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Verify employee (set status to VERIFIED)
    @PatchMapping("/{id}/verify")
    public ResponseEntity<EmployeeEntity> verifyEmployee(@PathVariable Long id) {
        EmployeeEntity verifiedEmployee = employeeService.verifyEmployee(id);
        if (verifiedEmployee != null) {
            return ResponseEntity.ok(verifiedEmployee);
        }
        return ResponseEntity.notFound().build();
    }

    // Fetch employees by status (Pending or Verified)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeEntity>> getEmployeesByStatus(@PathVariable EmployeeStatus status) {
        List<EmployeeEntity> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }
}*/
