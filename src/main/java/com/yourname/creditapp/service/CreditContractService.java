package com.yourname.creditapp.service;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.repository.interfaces.CreditContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor // Lombok автоматически создаёт конструктор для final полей
public class CreditContractService {

    private final CreditContractRepository contractRepository;

    @Transactional
    public CreditContract signContract(CreditApplication application) {
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
                .orElseThrow(() -> new RuntimeException("Договор с ID " + id + " не найден"));
    }
}