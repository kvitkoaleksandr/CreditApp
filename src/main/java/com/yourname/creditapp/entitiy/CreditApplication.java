package com.yourname.creditapp.entitiy;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "credit_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDate createdDate = LocalDate.now();

    @NonNull
    private String fullName;

    private String passportData;
    private String maritalStatus;
    private String address;
    private String phone;
    private String employmentDuration;
    private String jobTitle;
    private String companyName;
    private Double requestedAmount;
    private String decisionStatus;
    private Integer approvedTermMonths;
    private Double approvedAmount;
}