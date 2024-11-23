package com.yourname.creditapp.repository.impl;


import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.repository.interfaces.CreditContractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Указывает, что это компонент для работы с базой данных.
@Transactional // Обеспечивает автоматическое управление транзакциями
// Гарантирует, что операции с базой данных будут выполняться в рамках транзакции.
public class CreditContractRepositoryImpl implements CreditContractRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public CreditContract save(CreditContract contract) {
        if (contract.getId() == null) {
            entityManager.persist(contract); // Сохраняем новый договор
        } else {
            entityManager.merge(contract); // Обновляем существующий договор
        }
        return contract;
    }

    @Override
    public Optional<CreditContract> findById(Long id) {
        return Optional.ofNullable(entityManager.find(CreditContract.class, id));
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove); // Если договор найден, удаляем его
    }
}