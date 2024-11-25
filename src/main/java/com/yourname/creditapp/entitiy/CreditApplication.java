package com.yourname.creditapp.entitiy;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "credit_applications")
@Data
public class CreditApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String passportData;
    private String maritalStatus;
    private String address;
    private String phone;

    private String employmentDuration; // Можно оставить пустым, если не обязательно на этапе создания.
    private String jobTitle; // Должность, оставить пустым при создании.
    private String companyName; // Название организации.

    private Double requestedAmount;

    private String decisionStatus = "В ожидании"; // По умолчанию, статус заявки до принятия решения.
    private Integer approvedTermMonths; // Определяется только при одобрении заявки.
    private Double approvedAmount; // Определяется только при одобрении заявки.

    @Column(updatable = false)
    private LocalDate createdDate = LocalDate.now(); // Автоматически назначается текущая дата при создании.
}