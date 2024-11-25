package com.yourname.creditapp.repository.impl;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.repository.interfaces.CreditApplicationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Аннотация @Repository указывает, что этот класс является компонентом, работающим с базой данных.
@Repository
@Transactional // Обеспечивает автоматическое управление транзакциями.
public class CreditApplicationRepositoryImpl implements CreditApplicationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationRepositoryImpl.class);

    // Поиск последней заявки клиента по ФИО и паспортным данным
    @Override
    public Optional<CreditApplication> findLatestApplicationByClient(String fullName, String passportData) {
        log.debug("Поиск последней заявки клиента с ФИО: {} и паспортными данными: {}", fullName, passportData);
        return entityManager.createQuery(
                        "SELECT c FROM CreditApplication c WHERE c.fullName = :fullName AND c.passportData = :passportData ORDER BY c.createdDate DESC",
                        CreditApplication.class)
                .setParameter("fullName", fullName)
                .setParameter("passportData", passportData)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // Получение всех заявок
    @Override
    public List<CreditApplication> findAll() {
        log.debug("Получение всех кредитных заявок из базы данных.");
        return entityManager.createQuery("SELECT c FROM CreditApplication c", CreditApplication.class).getResultList();
    }

    // Получение всех одобренных заявок
    @Override
    public List<CreditApplication> findApprovedApplications() {
        log.debug("Получение всех одобренных кредитных заявок.");
        return entityManager.createQuery(
                        "SELECT c FROM CreditApplication c WHERE c.decisionStatus = :status", CreditApplication.class)
                .setParameter("status", "Одобрен")
                .getResultList();
    }

    // Сохранение новой заявки
    @Override
    public CreditApplication save(CreditApplication application) {
        if (application.getId() == null) {
            entityManager.persist(application);
        } else {
            // Обновляем только если заявка существует
            entityManager.merge(application);
        }
        return application;
    }

    // Поиск заявки по ID
    @Override
    public Optional<CreditApplication> findById(Long id) {
        log.debug("Поиск кредитной заявки с ID: {}", id);
        CreditApplication application = entityManager.find(CreditApplication.class, id);
        if (application == null) {
            log.warn("Заявка с ID {} не найдена.", id);
        }
        return Optional.ofNullable(application);
    }

    // Удаление заявки по ID
    @Override
    public void deleteById(Long id) {
        log.info("Удаление кредитной заявки с ID: {}", id);
        findById(id).ifPresentOrElse(
                entityManager::remove,
                () -> log.warn("Заявка с ID {} не найдена. Удаление невозможно.", id)
        );
    }

    // Удаление заявки по объекту
    @Override
    public void delete(CreditApplication application) {
        log.info("Удаление кредитной заявки для клиента: {}", application.getFullName());
        entityManager.remove(entityManager.contains(application) ? application : entityManager.merge(application));
    }
}