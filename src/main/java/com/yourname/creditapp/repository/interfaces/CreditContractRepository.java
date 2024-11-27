package com.yourname.creditapp.repository.interfaces;

import com.yourname.creditapp.entitiy.CreditContract;

import java.util.List;
import java.util.Optional;

public interface CreditContractRepository {

    List<CreditContract> findSignedContracts();

    Optional<CreditContract> findByApplicationId(Long applicationId);

    CreditContract save(CreditContract contract);

    Optional<CreditContract> findById(Long id);

    void deleteById(Long id);
}