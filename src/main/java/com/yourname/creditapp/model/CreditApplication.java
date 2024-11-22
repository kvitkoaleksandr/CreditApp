package com.yourname.creditapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "credit_applications")
@Data
public class CreditApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор
}
