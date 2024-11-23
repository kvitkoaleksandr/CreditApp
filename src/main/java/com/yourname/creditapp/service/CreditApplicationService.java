package com.yourname.creditapp.service;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.repository.interfaces.CreditApplicationRepository;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Аннотация @Service указывает, что это сервисный класс для обработки логики.
@Service
@RequiredArgsConstructor // Lombok автоматически создаёт конструктор для всех final полей
public class CreditApplicationService {

    private final CreditApplicationRepository repository;

    public List<CreditApplication> getApprovedApplications() {
        return repository.findApprovedApplications();
    }

    public List<CreditApplication> searchApplications(String query) {
        return repository.findAll().stream()
                .filter(application ->
                        application.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                                application.getPhone().contains(query) ||
                                application.getPassportData().contains(query))
                .toList();
    }

    @Transactional
    public CreditApplication makeDecision(Long applicationId) {
        // Получить заявку из базы данных
        CreditApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Заявка с ID " + applicationId + " не найдена"));

        // Случайное решение
        boolean isApproved = Math.random() > 0.5; // 50% вероятность
        application.setDecisionStatus(isApproved ? "Одобрен" : "Не одобрен");

        if (isApproved) {
            // Если одобрено, назначить срок и сумму
            application.setApprovedTermMonths((int) (Math.random() * 12) + 1); // От 1 до 12 месяцев
            application.setApprovedAmount(application.getRequestedAmount() * (0.8 + Math.random() * 0.4)); // 80-120% от запрошенной суммы
        } else {
            // Если не одобрено, обнулить данные
            application.setApprovedTermMonths(null);
            application.setApprovedAmount(null);
        }

        // Сохранить изменения
        repository.save(application);

        return application; // Вернуть обновленную заявку
    }

    // Метод для создания новой заявки
    public CreditApplication createApplication(CreditApplication application) {
        return repository.save(application);
    }

    // Метод для поиска заявки по ID
    public CreditApplication getApplicationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка с ID " + id + " не найдена"));
    }

    // Метод для получения всех заявок
    public List<CreditApplication> getAllApplications() {
        return repository.findAll();
    }

    // Метод для удаления заявки по ID
    public void deleteApplication(Long id) {
        repository.deleteById(id);
    }
}
