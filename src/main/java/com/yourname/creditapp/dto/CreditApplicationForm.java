package com.yourname.creditapp.dto;

import lombok.Data;

@Data
public class CreditApplicationForm {
    private String firstName;     // Имя
    private String lastName;      // Фамилия
    private String middleName;    // Отчество

    private String city;          // Город
    private String street;        // Улица
    private String houseNumber;   // Номер дома

    private String phone;         // Телефон
    private String maritalStatus; // Семейное положение

    private String employmentDuration; // Срок занятости
    private String jobTitle;           // Должность
    private String companyName;        // Название компании

    private String passportData;       // Паспортные данные

    private Double requestedAmount;    // Желаемая сумма кредита
}