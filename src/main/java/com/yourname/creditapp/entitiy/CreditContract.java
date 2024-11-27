package com.yourname.creditapp.entitiy;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "credit_contracts")
@Data
public class CreditContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private CreditApplication creditApplication;

    private LocalDate signingDate;
    private String contractStatus;
}