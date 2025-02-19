package com.app.onboarding.service;

import com.app.onboarding.dto.RegistrationLoginDto.EmployeeRequestDto;
import com.app.onboarding.model.EmployeeEntity;
import com.app.onboarding.model.EmployeeStatus;
import com.app.onboarding.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Create a new employee with status "PENDING_VERIFICATION" by default
    public EmployeeEntity createEmployee(EmployeeRequestDto employeeRequestDTO) {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setFullName(employeeRequestDTO.getFullName());
        employee.setContactInfo(employeeRequestDTO.getContactInfo());
        employee.setPosition(employeeRequestDTO.getPosition());
        employee.setDepartment(employeeRequestDTO.getDepartment());
        employee.setEmail(employeeRequestDTO.getEmail());
        employee.setEmployeeType(employeeRequestDTO.getEmployeeType());
        employee.setDateJoined(employeeRequestDTO.getDateJoined());
        employee.setDob(employeeRequestDTO.getDob());

        // Set default status to PENDING_VERIFICATION if not provided
        if (employeeRequestDTO.getStatus() == null) {
            employee.setStatus(EmployeeStatus.PENDING_VERIFICATION); // Default status
        } else {
            employee.setStatus(employeeRequestDTO.getStatus());
        }

        return employeeRepository.save(employee);
    }

    // Edit existing employee
    public EmployeeEntity editEmployee(Long id, EmployeeEntity employeeDetails) {
        Optional<EmployeeEntity> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeEntity existingEmployee = employee.get();

            // Check if the employee is deleted
            if (existingEmployee.isDeleted()) {
                throw new IllegalStateException("Cannot edit a deleted employee.");
            }

            // Check if the employee is verified before allowing edit
            if (existingEmployee.getStatus() == EmployeeStatus.VERIFIED) {
                throw new IllegalStateException("Cannot update a VERIFIED employee.");
            }

            // Update only allowed fields
            existingEmployee.setFullName(employeeDetails.getFullName());
            existingEmployee.setContactInfo(employeeDetails.getContactInfo());
            existingEmployee.setPosition(employeeDetails.getPosition());
            existingEmployee.setDepartment(employeeDetails.getDepartment());

            return employeeRepository.save(existingEmployee);
        }
        throw new IllegalArgumentException("Employee not found with ID: " + id);
    }

    // Soft delete employee (mark as deleted)
    public void softDeleteEmployee(Long id) {
        Optional<EmployeeEntity> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeEntity existingEmployee = employee.get();

            // Check if the employee is already deleted
            if (existingEmployee.isDeleted()) {
                throw new IllegalStateException("Employee is already deleted.");
            }

            // Check if the employee is verified before allowing delete
            if (existingEmployee.getStatus() == EmployeeStatus.VERIFIED) {
                throw new IllegalStateException("Cannot delete a VERIFIED employee.");
            }

            existingEmployee.setIsDeleted(true); // Mark as deleted
            employeeRepository.save(existingEmployee);
        } else {
            throw new IllegalArgumentException("Employee not found with ID: " + id);
        }
    }

    // Verify employee (set status to VERIFIED)
    public EmployeeEntity verifyEmployee(Long id) {
        Optional<EmployeeEntity> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeEntity existingEmployee = employee.get();

            // Check if the employee is deleted
            if (existingEmployee.isDeleted()) {
                throw new IllegalStateException("Cannot verify a deleted employee.");
            }

            existingEmployee.setStatus(EmployeeStatus.VERIFIED); // Set status to VERIFIED
            return employeeRepository.save(existingEmployee);
        }
        throw new IllegalArgumentException("Employee not found with ID: " + id);
    }

    // Fetch employees by status (Pending or Verified)
    public List<EmployeeEntity> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }

    // Fetch all employees that are not deleted
    public List<EmployeeEntity> getAllActiveEmployees() {
        return employeeRepository.findByIsDeletedFalse();
    }

    // Fetch all deleted employees
    public List<EmployeeEntity> getDeletedEmployees() {
        return employeeRepository.findByIsDeletedTrue();
    }
}





