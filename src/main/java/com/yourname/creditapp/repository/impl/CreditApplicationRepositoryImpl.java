package com.yourname.creditapp.repository.impl;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.repository.interfaces.CreditApplicationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Аннотация @Repository указывает, что этот класс является компонентом, работающим с базой данных.
@Repository
@Transactional // Обеспечивает автоматическое управление транзакциями.
public class CreditApplicationRepositoryImpl implements CreditApplicationRepository {
    // Аннотация @PersistenceContext автоматически предоставляет EntityManager для работы с базой данных.
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CreditApplication> findApprovedApplications() {
        return entityManager.createQuery(
                        "SELECT c FROM CreditApplication c WHERE c.decisionStatus = :status", CreditApplication.class)
                .setParameter("status", "Одобрен")
                .getResultList();
    }

    @Override
    public CreditApplication save(CreditApplication application) {
        if (application.getId() == null) {
            // Если ID нет, это новая заявка — сохраняем через persist.
            entityManager.persist(application);
        } else {
            // Если ID есть, обновляем существующую заявку через merge.
            entityManager.merge(application);
        }
        return application;
    }

    @Override
    public Optional<CreditApplication> findById(Long id) {
        CreditApplication application = entityManager.find(CreditApplication.class, id);
        return Optional.ofNullable(application);
    }

    @Override
    public List<CreditApplication> findAll() {
        // Выполняем JPQL-запрос для получения всех заявок.
        return entityManager.createQuery("SELECT c FROM CreditApplication c", CreditApplication.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        // Сначала находим заявку, затем удаляем её.
        findById(id).ifPresent(entityManager::remove);
    }
}
