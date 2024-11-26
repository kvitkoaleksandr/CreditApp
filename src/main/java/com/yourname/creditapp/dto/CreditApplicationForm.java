package com.yourname.creditapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreditApplicationForm {
    @NotBlank(message = "Фамилия обязательна для заполнения")
    private String lastName;

    @NotBlank(message = "Имя обязательно для заполнения")
    private String firstName;

    @NotBlank(message = "Отчество обязательно для заполнения")
    private String middleName;

    @NotBlank(message = "Паспортные данные обязательны для заполнения")
    private String passportData;

    @NotBlank(message = "Город обязателен для заполнения")
    private String city;

    @NotBlank(message = "Улица обязательна для заполнения")
    private String street;

    @NotBlank(message = "Номер дома обязателен для заполнения")
    private String houseNumber;

    @NotBlank(message = "Телефон обязателен для заполнения")
    private String phone;

    @NotBlank(message = "Семейное положение обязательно для заполнения")
    private String maritalStatus;

    @NotBlank(message = "Срок занятости обязателен для заполнения")
    private String employmentDuration;

    @NotBlank(message = "Должность обязательна для заполнения")
    private String jobTitle;

    @NotBlank(message = "Название компании обязательно для заполнения")
    private String companyName;

    @NotNull(message = "Сумма кредита обязательна")
    @Positive(message = "Сумма кредита должна быть положительной")
    private Double requestedAmount;
}