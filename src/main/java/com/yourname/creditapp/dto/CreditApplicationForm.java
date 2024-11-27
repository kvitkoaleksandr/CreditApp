package com.yourname.creditapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreditApplicationForm {
    private static final String REQUIRED_MESSAGE = " обязательно для заполнения";

    @NotBlank(message = "Фамилия" + REQUIRED_MESSAGE)
    private String lastName;

    @NotBlank(message = "Имя" + REQUIRED_MESSAGE)
    private String firstName;

    @NotBlank(message = "Отчество" + REQUIRED_MESSAGE)
    private String middleName;

    @NotBlank(message = "Паспортные данные" + REQUIRED_MESSAGE)
    private String passportData;

    @NotBlank(message = "Город" + REQUIRED_MESSAGE)
    private String city;

    @NotBlank(message = "Улица" + REQUIRED_MESSAGE)
    private String street;

    @NotBlank(message = "Номер дома" + REQUIRED_MESSAGE)
    private String houseNumber;

    @NotBlank(message = "Телефон" + REQUIRED_MESSAGE)
    private String phone;

    @NotBlank(message = "Семейное положение" + REQUIRED_MESSAGE)
    private String maritalStatus;

    @NotBlank(message = "Срок занятости" + REQUIRED_MESSAGE)
    private String employmentDuration;

    @NotBlank(message = "Должность" + REQUIRED_MESSAGE)
    private String jobTitle;

    @NotBlank(message = "Название компании" + REQUIRED_MESSAGE)
    private String companyName;

    @NotNull(message = "Сумма кредита обязательна")
    @Positive(message = "Сумма кредита должна быть положительной")
    private Double requestedAmount;
}