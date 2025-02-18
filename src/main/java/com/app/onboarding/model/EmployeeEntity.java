package com.app.onboarding.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")

public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String contactInfo;

    private String position;

    private String department;

    @Enumerated(EnumType.STRING) // Store enum as String in the database
    private EmployeeStatus status;

    private String email;

    private String employeeType;

    private LocalDate dateJoined;

    private LocalDate dob;

    @Column(nullable = false)
    private boolean isDeleted = false; // Soft delete field

    // Getters and setters for other fields

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}