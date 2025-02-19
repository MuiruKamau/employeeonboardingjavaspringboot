package com.app.onboarding.controller;

import com.app.onboarding.dto.RegistrationLoginDto.EmployeeRequestDto;
import com.app.onboarding.dto.RegistrationLoginDto.EmployeeUpdateDto;
import com.app.onboarding.model.EmployeeEntity;
import com.app.onboarding.model.EmployeeStatus;
import com.app.onboarding.service.EmployeeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/employees")
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
    @PutMapping("/update/{id}")
    public ResponseEntity<?> editEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateDto updateDto) {
        try {
            EmployeeEntity updatedEmployee = employeeService.editEmployee(id, updateDto);
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
    @DeleteMapping("/delete/{id}")
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
    @PatchMapping("/verify/{id}")
    public ResponseEntity<?> verifyEmployee(@PathVariable Long id) {
        try {
            EmployeeEntity verifiedEmployee = employeeService.verifyEmployee(id);
            return ResponseEntity.ok(verifiedEmployee);
        } catch (IllegalStateException e) {
            // Handle cases where the employee is deleted or cannot be verified
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Handle cases where the employee is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error verifying employee: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying employee: " + e.getMessage());
        }
    }

    // Fetch employees by status (Pending or Verified)
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getEmployeesByStatus(@Parameter(
            description = "Employee status",
            required = true,
            schema = @Schema(allowableValues = {"VERIFIED", "PENDING_VERIFICATION"})
    ) @PathVariable String status) {
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
    @GetMapping("/all")
    public ResponseEntity<?> getAllActiveEmployees() {
        try {
            List<EmployeeEntity> employees = employeeService.getAllActiveEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            logger.error("Error fetching active employees: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching active employees: " + e.getMessage());
        }
    }

    // Fetch all deleted employees
    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedEmployees() {
        try {
            List<EmployeeEntity> deletedEmployees = employeeService.getDeletedEmployees();
            return ResponseEntity.ok(deletedEmployees);
        } catch (Exception e) {
            logger.error("Error fetching deleted employees: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching deleted employees: " + e.getMessage());
        }
    }
}



