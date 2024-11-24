package com.yourname.creditapp.service;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.exception.EntityNotFoundException;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.repository.interfaces.CreditContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok автоматически создаёт конструктор для final полей
public class CreditContractService {

    private final CreditContractRepository contractRepository;

    public List<CreditContract> getSignedContracts() {
        return contractRepository.findSignedContracts();
    }

    @Transactional
    public CreditContract signContract(CreditApplication application) {
        // Проверяем, существует ли договор для данной заявки
        Optional<CreditContract> existingContract = contractRepository.findByApplicationId(application.getId());
        if (existingContract.isPresent()) {
            throw new InvalidActionException("Договор для заявки с ID " + application.getId() + " уже существует.");
        }
        // Создаём новый договор
        CreditContract contract = new CreditContract();
        contract.setCreditApplication(application); // Связываем с заявкой
        contract.setSigningDate(LocalDate.now()); // Устанавливаем дату подписания
        contract.setContractStatus("Подписан"); // Устанавливаем статус договора

        // Сохраняем договор через репозиторий
        return contractRepository.save(contract);
    }

    public CreditContract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Договор с ID " + id + " не найден"));
    }
}