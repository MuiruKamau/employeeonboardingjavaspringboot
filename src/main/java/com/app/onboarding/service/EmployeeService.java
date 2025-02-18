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

    public EmployeeEntity editEmployee(Long id, EmployeeEntity employeeDetails) {
        Optional<EmployeeEntity> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeEntity existingEmployee = employee.get();
            existingEmployee.setFullName(employeeDetails.getFullName());
            existingEmployee.setContactInfo(employeeDetails.getContactInfo());
            existingEmployee.setPosition(employeeDetails.getPosition());
            existingEmployee.setDepartment(employeeDetails.getDepartment());
            return employeeRepository.save(existingEmployee);
        }
        return null; // Handle this with an exception in real-world use case
    }

    public void softDeleteEmployee(Long id) {
        Optional<EmployeeEntity> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeEntity existingEmployee = employee.get();
            existingEmployee.setIsDeleted(true); // Mark as deleted
            employeeRepository.save(existingEmployee);
        }
    }

    public EmployeeEntity verifyEmployee(Long id) {
        Optional<EmployeeEntity> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeEntity existingEmployee = employee.get();
            existingEmployee.setStatus(EmployeeStatus.VERIFIED); // Set status to VERIFIED
            return employeeRepository.save(existingEmployee);
        }
        return null;
    }

    public List<EmployeeEntity> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status); // Query by enum (convert to String)
    }
}

