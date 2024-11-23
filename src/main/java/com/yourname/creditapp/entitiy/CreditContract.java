package com.yourname.creditapp.entitiy;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity //@Entity — отмечает класс как сущность базы данных.
@Table(name = "credit_contracts") //@Table(name = "credit_contracts") — указывает имя таблицы в базе данных.
@Data
public class CreditContract {
//@Id и @GeneratedValue(strategy = GenerationType.IDENTITY)— идентификатор договора,
// который будет автоматически генерироваться.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор договора

    @OneToOne // Связь 1 к 1 с заявкой — устанавливает связь "один к одному" между договором и заявкой:
    @JoinColumn(name = "application_id", nullable = false)
    private CreditApplication creditApplication; // Ссылка на заявку

    private LocalDate signingDate; // Дата подписания договора
    private String contractStatus; // Статус договора (Подписан / Не подписан)
}