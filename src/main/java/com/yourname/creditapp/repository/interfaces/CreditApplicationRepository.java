package com.yourname.creditapp.repository.interfaces;

import com.yourname.creditapp.entitiy.CreditApplication;

import java.util.List;
import java.util.Optional;

public interface CreditApplicationRepository {

    void delete(CreditApplication application);

    Optional<CreditApplication> findLatestApplicationByClient(String fullName, String passportData);

    List<CreditApplication> findApprovedApplications();

    CreditApplication save(CreditApplication application);

    Optional<CreditApplication> findById(Long id);

    List<CreditApplication> findAll();

    void deleteById(Long id);
}