package com.app.onboarding.controller;

import com.app.onboarding.dto.RegistrationLoginDto.EmployeeRequestDto;
import com.app.onboarding.model.EmployeeEntity;
import com.app.onboarding.model.EmployeeStatus;
import com.app.onboarding.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> onboardEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        try {
            EmployeeEntity createdEmployee = employeeService.createEmployee(employeeRequestDto);
            return ResponseEntity.ok(createdEmployee);
        } catch (Exception e) {
            logger.error("Error creating employee: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating employee: " + e.getMessage());
        }
    }

    // Edit existing employee
    @PutMapping("/{id}")
    public ResponseEntity<?> editEmployee(@PathVariable Long id, @RequestBody EmployeeEntity employeeDetails) {
        try {
            EmployeeEntity updatedEmployee = employeeService.editEmployee(id, employeeDetails);
            return ResponseEntity.ok(updatedEmployee);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating employee: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating employee: " + e.getMessage());
        }
    }

    // Soft delete employee (mark as deleted)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteEmployee(@PathVariable Long id) {
        try {
            employeeService.softDeleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting employee: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting employee: " + e.getMessage());
        }
    }

    // Verify employee (set status to VERIFIED)
    @PatchMapping("/{id}/verify")
    public ResponseEntity<?> verifyEmployee(@PathVariable Long id) {
        try {
            EmployeeEntity verifiedEmployee = employeeService.verifyEmployee(id);
            return ResponseEntity.ok(verifiedEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error verifying employee: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying employee: " + e.getMessage());
        }
    }

    // Fetch employees by status (Pending or Verified)
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getEmployeesByStatus(@PathVariable String status) {
        try {
            EmployeeStatus employeeStatus = EmployeeStatus.valueOf(status); // Convert string to enum
            List<EmployeeEntity> employees = employeeService.getEmployeesByStatus(employeeStatus);
            return ResponseEntity.ok(employees);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status: {}", status, e);
            return ResponseEntity.badRequest().body("Invalid status: " + status + ". Allowed values are VERIFIED and PENDING_VERIFICATION.");
        } catch (Exception e) {
            logger.error("Error fetching employees by status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching employees by status: " + e.getMessage());
        }
    }

    // Fetch all active employees
    @GetMapping
    public ResponseEntity<?> getAllActiveEmployees() {
        try {
            List<EmployeeEntity> employees = employeeService.getAllActiveEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            logger.error("Error fetching active employees: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching active employees: " + e.getMessage());
        }
    }
}
/*package com.app.onboarding.controller;

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
        } else {
            return ResponseEntity.status(403).body(null);  // Forbidden if employee is not verified
        }
    }

    // Soft delete employee (mark as deleted)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteEmployee(@PathVariable Long id) {
        employeeService.softDeleteEmployee(id);
        return ResponseEntity.noContent().build();  // Forbidden if employee is not verified
    }

    // Verify employee (set status to VERIFIED)
    @PatchMapping("/{id}/verify")
    public ResponseEntity<EmployeeEntity> verifyEmployee(@PathVariable Long id) {
        EmployeeEntity verifiedEmployee = employeeService.verifyEmployee(id);
        if (verifiedEmployee != null) {
            return ResponseEntity.ok(verifiedEmployee);
        }
        return ResponseEntity.notFound().build(); // Employee not found
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

*/



