package com.yourname.creditapp.service;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.exception.EntityNotFoundException;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.repository.interfaces.CreditApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

// Аннотация @Service указывает, что это сервисный класс для обработки логики.
@Service
@RequiredArgsConstructor
public class CreditApplicationService {

    private final CreditApplicationRepository repository;

    @Transactional
    public CreditApplication createApplication(CreditApplication application) {
        // Проверяем, есть ли предыдущая заявка
        Optional<CreditApplication> latestApplication = findLatestApplication(application.getFullName(), application.getPassportData());

        if (latestApplication.isPresent()) {
            CreditApplication previousApplication = latestApplication.get();
            long daysSinceLastApplication = ChronoUnit.DAYS.between(previousApplication.getCreatedDate(), LocalDate.now());

            if (daysSinceLastApplication < 28) {
                long daysLeft = 28 - daysSinceLastApplication;
                throw new InvalidActionException(
                        "Вы не можете подать новую заявку. До следующей подачи осталось " + daysLeft + " дней."
                );
            }
        }

        // Если проверка пройдена, сохраняем новую заявку
        application.setCreatedDate(LocalDate.now()); // Устанавливаем дату создания
        return repository.save(application);
    }

    public Optional<CreditApplication> findLatestApplication(String fullName, String passportData) {
        return repository.findLatestApplicationByClient(fullName, passportData);
    }

    public List<CreditApplication> searchApplications(String query) {
        // Получаем все заявки из репозитория
        List<CreditApplication> allApplications = repository.findAll();

        // Фильтруем заявки на основе логики "ИЛИ"
        return allApplications.stream()
                .filter(application ->
                        application.getFullName().toLowerCase().contains(query.toLowerCase()) || // Поиск по ФИО
                                application.getPhone().replace("+", "").contains(query.replace("+", "")) || // Поиск по телефону
                                application.getPassportData().contains(query) // Поиск по паспортным данным
                )
                .toList();
    }

    public List<CreditApplication> getApprovedApplications() {
        return repository.findApprovedApplications();
    }

    @Transactional
    public CreditApplication makeDecision(Long applicationId) {
        // Получить заявку из базы данных
        CreditApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + applicationId + " не найдена"));

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

    public CreditApplication getApplicationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + id + " не найдена"));
    }

    public List<CreditApplication> getAllApplications() {
        return repository.findAll();
    }

    public void deleteApplication(Long id) {
        repository.deleteById(id);
    }
}