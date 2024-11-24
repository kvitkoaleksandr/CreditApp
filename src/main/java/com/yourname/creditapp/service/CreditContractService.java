package com.yourname.creditapp.service;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.exception.EntityNotFoundException;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.repository.interfaces.CreditContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Аннотация @Service указывает, что это сервисный класс для обработки логики.
@Service
@RequiredArgsConstructor
public class CreditContractService {

    private static final Logger log = LoggerFactory.getLogger(CreditContractService.class); // Создаём логгер
    private final CreditContractRepository contractRepository;

    // Получение всех подписанных договоров
    public List<CreditContract> getSignedContracts() {
        log.info("Запрос на получение всех подписанных кредитных договоров.");
        return contractRepository.findSignedContracts();
    }

    @Transactional
    public CreditContract signContract(CreditApplication application) {
        log.info("Подписание договора по заявке с ID: {}", application.getId());

        // Проверяем, существует ли договор для данной заявки
        Optional<CreditContract> existingContract = contractRepository.findByApplicationId(application.getId());
        if (existingContract.isPresent()) {
            log.warn("Договор для заявки с ID {} уже существует. Повторное подписание невозможно.", application.getId());
            throw new InvalidActionException("Договор для заявки с ID " + application.getId() + " уже существует.");
        }

        // Создаём новый договор
        CreditContract contract = new CreditContract();
        contract.setCreditApplication(application); // Связываем с заявкой
        contract.setSigningDate(LocalDate.now()); // Устанавливаем дату подписания
        contract.setContractStatus("Подписан"); // Устанавливаем статус договора

        log.info("Создан новый договор для заявки с ID: {}", application.getId());
        // Сохраняем договор через репозиторий
        return contractRepository.save(contract);
    }

    // Получение договора по ID
    public CreditContract getContractById(Long id) {
        log.debug("Получение договора с ID: {}", id);
        return contractRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Договор с ID {} не найден.", id);
                    return new EntityNotFoundException("Договор с ID " + id + " не найден");
                });
    }
}