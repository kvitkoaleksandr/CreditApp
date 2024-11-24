package com.yourname.creditapp.entitiy;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity // Аннотация @Entity отмечает класс как сущность базы данных.
@Table(name = "credit_applications") // Указывает имя таблицы в базе данных.
@Data // Аннотация Lombok для генерации геттеров, сеттеров и других методов.
public class CreditApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация ID.
    private Long id; // Уникальный идентификатор заявки.

    private String fullName; // Полное имя клиента (ФИО).
    private String passportData; // Паспортные данные клиента.
    private String maritalStatus; // Семейное положение.
    private String address; // Адрес прописки.
    private String phone; // Контактный телефон.

    private String employmentDuration; // Период работы (стаж).
    private String jobTitle; // Должность.
    private String companyName; // Название организации.

    private Double requestedAmount; // Сумма желаемого кредита.

    private String decisionStatus; // Статус решения (Одобрен / Не одобрен).
    private Integer approvedTermMonths; // Одобренный срок в месяцах.
    private Double approvedAmount; // Одобренная сумма кредита.

    private LocalDate createdDate; // Дата создания заявки.
}