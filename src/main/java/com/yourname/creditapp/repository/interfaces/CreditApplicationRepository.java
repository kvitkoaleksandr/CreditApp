package com.yourname.creditapp.repository.interfaces;

import com.yourname.creditapp.entitiy.CreditApplication;

import java.util.List;
import java.util.Optional;

public interface CreditApplicationRepository {

    void delete(CreditApplication application);

    Optional<CreditApplication> findLatestApplicationByClient(String fullName, String passportData);

    // для получения всех одобренных заявок:
    List<CreditApplication> findApprovedApplications();

    // Метод для сохранения заявки
    CreditApplication save(CreditApplication application);

    // Метод для поиска заявки по ID
    Optional<CreditApplication> findById(Long id);

    // Метод для получения всех заявок
    List<CreditApplication> findAll();

    // Метод для удаления заявки по ID
    void deleteById(Long id);
}
