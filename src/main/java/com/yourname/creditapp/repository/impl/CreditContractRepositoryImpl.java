package com.yourname.creditapp.repository.impl;


import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.repository.interfaces.CreditContractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CreditContractRepositoryImpl implements CreditContractRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(CreditContractRepositoryImpl.class);

    @Override
    public List<CreditContract> findSignedContracts() {
        log.debug("Получение всех подписанных кредитных договоров из базы данных.");
        return entityManager.createQuery(
                        "SELECT c FROM CreditContract c " +
                                "WHERE c.contractStatus = :status", CreditContract.class)
                .setParameter("status", "Подписан")
                .getResultList();
    }

    @Override
    public Optional<CreditContract> findByApplicationId(Long applicationId) {
        log.debug("Поиск договора по ID заявки: {}", applicationId);
        return entityManager.createQuery(
                        "SELECT c FROM CreditContract c " +
                                "WHERE c.creditApplication.id = :applicationId", CreditContract.class)
                .setParameter("applicationId", applicationId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public CreditContract save(CreditContract contract) {
        if (contract.getId() == null) {
            log.info("Сохранение нового договора для заявки с ID: {}", contract.getCreditApplication().getId());
            entityManager.persist(contract);
            log.debug("Договор успешно сохранён с новым ID: {}", contract.getId());
        } else {
            log.info("Обновление договора с ID: {}", contract.getId());
            entityManager.merge(contract);
        }
        return contract;
    }

    @Override
    public Optional<CreditContract> findById(Long id) {
        log.debug("Поиск кредитного договора с ID: {}", id);
        CreditContract contract = entityManager.find(CreditContract.class, id);
        if (contract == null) {
            log.warn("Кредитный договор с ID {} не найден.", id);
        }
        return Optional.ofNullable(contract);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Удаление кредитного договора с ID: {}", id);
        findById(id).ifPresentOrElse(
                entityManager::remove,
                () -> log.warn("Кредитный договор с ID {} не найден. Удаление невозможно.", id)
        );
    }
}