package com.yourname.creditapp.service;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.exception.EntityNotFoundException;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.repository.interfaces.CreditApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

// Аннотация @Service указывает, что это сервисный класс для обработки логики.
@Service
@RequiredArgsConstructor
public class CreditApplicationService {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationService.class); // Создаём логгер
    private final CreditApplicationRepository repository;

    @Transactional
    public CreditApplication createApplication(CreditApplication application) {
        log.info("Создание новой кредитной заявки для клиента: {}", application.getFullName());

        // Проверяем, есть ли предыдущая заявка
        Optional<CreditApplication> latestApplication = findLatestApplication(application.getFullName(), application.getPassportData());

        if (latestApplication.isPresent()) {
            CreditApplication previousApplication = latestApplication.get();
            long daysSinceLastApplication = ChronoUnit.DAYS.between(previousApplication.getCreatedDate(), LocalDate.now());

            if (daysSinceLastApplication < 28) {
                long daysLeft = 28 - daysSinceLastApplication;
                log.warn("Клиент {} не может подать заявку. До повторной подачи осталось {} дней.",
                        application.getFullName(), daysLeft);
                throw new InvalidActionException(
                        "Вы не можете подать новую заявку. До следующей подачи осталось " + daysLeft + " дней."
                );
            }
        }

        // Если проверка пройдена, сохраняем новую заявку
        application.setCreatedDate(LocalDate.now()); // Устанавливаем дату создания
        log.info("Заявка для клиента {} успешно создана.", application.getFullName());
        return repository.save(application);
    }

    // Поиск последней заявки клиента по ФИО и паспортным данным
    public Optional<CreditApplication> findLatestApplication(String fullName, String passportData) {
        return repository.findLatestApplicationByClient(fullName, passportData);
    }

    // Поиск заявок по запросу (ФИО, телефон, паспортные данные)
    public List<CreditApplication> searchApplications(String query) {
        log.info("Поиск заявок по запросу: {}", query);

        List<CreditApplication> results = repository.findAll().stream()
                .filter(application ->
                        application.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                                application.getPhone().replace("+", "").contains(query.replace("+", "")) ||
                                application.getPassportData().contains(query))
                .toList();

        log.info("По запросу '{}' найдено {} заявок.", query, results.size());
        return results;
    }

    // Получение всех одобренных заявок
    public List<CreditApplication> getApprovedApplications() {
        return repository.findApprovedApplications();
    }

    @Transactional
    public CreditApplication makeDecision(Long applicationId) {
        log.info("Принятие решения по заявке с ID: {}", applicationId);

        // Получаем заявку из базы данных
        CreditApplication application = repository.findById(applicationId)
                .orElseThrow(() -> {
                    log.error("Заявка с ID {} не найдена. Принятие решения невозможно.", applicationId);
                    return new EntityNotFoundException("Заявка с ID " + applicationId + " не найдена");
                });

        // Проверяем, было ли уже принято решение
        if (application.getDecisionStatus() != null) {
            log.warn("Решение по заявке с ID {} уже принято. Повторное изменение запрещено.", applicationId);
            throw new InvalidActionException("Решение по заявке уже принято.");
        }

        // Генерация случайного решения
        boolean isApproved = Math.random() > 0.5; // 50% вероятность
        application.setDecisionStatus(isApproved ? "Одобрен" : "Не одобрен");

        if (isApproved) {
            application.setApprovedTermMonths((int) (Math.random() * 12) + 1); // Срок от 1 до 12 месяцев
            application.setApprovedAmount(application.getRequestedAmount() * (0.8 + Math.random() * 0.4)); // Сумма 80–120%
            log.info("Заявка с ID {} одобрена. Срок: {} месяцев, сумма: {}.",
                    applicationId, application.getApprovedTermMonths(), application.getApprovedAmount());
        } else {
            log.info("Заявка с ID {} не одобрена.", applicationId);
        }

        // Сохраняем изменения
        repository.save(application);

        return application;
    }

    // Получение заявки по ID
    public CreditApplication getApplicationById(Long id) {
        log.debug("Получение кредитной заявки с ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + id + " не найдена"));
    }

    // Получение всех заявок
    public List<CreditApplication> getAllApplications() {
        return repository.findAll();
    }

    // Удаление заявки по ID
    public void deleteApplication(Long id) {
        log.info("Удаление заявки с ID: {}", id);

        // Проверяем, существует ли заявка
        CreditApplication application = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Заявка с ID {} не найдена. Удаление невозможно.", id);
                    return new EntityNotFoundException("Заявка с ID " + id + " не найдена");
                });

        // Удаляем заявку
        repository.delete(application);
        log.info("Заявка с ID {} успешно удалена.", id);
    }
}