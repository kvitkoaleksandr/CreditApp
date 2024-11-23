package com.yourname.creditapp.entitiy;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "credit_applications")
@Data
public class CreditApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор

    private String fullName; // ФИО
    private String passportData; // Паспортные данные
    private String maritalStatus; // Семейное положение
    private String address; // Адрес прописки
    private String phone; // Контактный телефон

    private String employmentDuration; // Период работы
    private String jobTitle; // Должность
    private String companyName; // Название организации

    private Double requestedAmount; // Сумма желаемого кредита

    private String decisionStatus; // Статус решения (Одобрен / Не одобрен)
    private Integer approvedTermMonths; // Срок в месяцах
    private Double approvedAmount; // Одобренная сумма
}
