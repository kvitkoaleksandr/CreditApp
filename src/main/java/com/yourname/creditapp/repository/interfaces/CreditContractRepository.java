package com.yourname.creditapp.repository.interfaces;

import com.yourname.creditapp.entitiy.CreditContract;

import java.util.Optional;

public interface CreditContractRepository {

    Optional<CreditContract> findByApplicationId(Long applicationId);
    // Сохранение или обновление договора в базу данных.
    //Если договор уже существует (ID не null), обновляет его.
    CreditContract save(CreditContract contract);

    // Поиск договора по ID.
    // Возвращает Optional, чтобы явно обработать случай, когда договор не найден.
    Optional<CreditContract> findById(Long id);

    // Удаление договора по ID
    void deleteById(Long id);
}