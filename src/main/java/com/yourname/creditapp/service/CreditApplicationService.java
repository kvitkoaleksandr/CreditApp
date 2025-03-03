package com.yourname.creditapp.service;

import com.yourname.creditapp.dto.CreditApplicationFormDto;
import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.exception.EntityNotFoundException;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.repository.interfaces.CreditApplicationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditApplicationService {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationService.class);
    private final CreditApplicationRepository repository;
    private static final int NUMBER_DAYS = 28;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void processApplicationDecision(Long applicationId) {
        CreditApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));

        if (application.getDecisionStatus() != null && !application.getDecisionStatus().equals("В ожидании")) {
            log.info("Решение по заявке с ID {} уже принято: {}", applicationId, application.getDecisionStatus());
            return;
        }

        String decision = Math.random() > 0.5 ? "Одобрен" : "Не одобрен";
        application.setDecisionStatus(decision);

        if ("Одобрен".equals(decision)) {
            application.setApprovedAmount(application.getRequestedAmount());
            application.setApprovedTermMonths((int) (1 + Math.random() * 12)); // Срок от 1 до 12 месяцев
        } else {
            application.setApprovedAmount(0.0);
            application.setApprovedTermMonths(0);
        }

        log.info("Заявка до обновления: {}", application);
        repository.save(application);
        log.info("Заявка после обновления: {}", application);
        log.info("Решение по заявке с ID {} принято: {}", applicationId, decision);
    }

    public CreditApplication convertFormToEntity(CreditApplicationFormDto form) {
        CreditApplication application = new CreditApplication();

        application.setFullName(form.getLastName() + " " + form.getFirstName() + " " + form.getMiddleName());

        application.setAddress("г. " + form.getCity() + ", ул. " + form.getStreet() + ", д. " + form.getHouseNumber());

        application.setPhone(form.getPhone());
        application.setMaritalStatus(form.getMaritalStatus());
        application.setEmploymentDuration(form.getEmploymentDuration());
        application.setJobTitle(form.getJobTitle());
        application.setCompanyName(form.getCompanyName());
        application.setPassportData(form.getPassportData());
        application.setRequestedAmount(form.getRequestedAmount());

        return application;
    }

    @Transactional
    public CreditApplication createApplicationFromForm(CreditApplicationFormDto form) {
        log.info("Создание новой кредитной заявки на основе формы для клиента: {} {}",
                form.getFirstName(), form.getLastName());

        CreditApplication application = convertFormToEntity(form);
        Optional<CreditApplication> latestApplication = findLatestApplication(application.getFullName(),
                form.getPassportData());

        if (latestApplication.isPresent()) {
            CreditApplication previousApplication = latestApplication.get();
            long daysSinceLastApplication = ChronoUnit.DAYS.between(previousApplication
                    .getCreatedDate(), LocalDate.now());

            if (daysSinceLastApplication < 28) {
                long daysLeft = NUMBER_DAYS - daysSinceLastApplication;
                log.warn("Клиент {} не может подать заявку. До повторной подачи осталось {} дней.",
                        application.getFullName(), daysLeft);
                throw new InvalidActionException(
                        "Вы не можете подать новую заявку. До следующей подачи осталось " + daysLeft + " дней."
                );
            }
        }

        application.setCreatedDate(LocalDate.now());
        log.info("Заявка для клиента {} успешно создана.", application.getFullName());
        return repository.save(application);
    }

    public Optional<CreditApplication> findLatestApplication(String fullName, String passportData) {
        return repository.findLatestApplicationByClient(fullName, passportData);
    }

    public List<CreditApplication> searchApplications(String query) {
        log.info("Поиск заявок по запросу: {}", query);

        List<CreditApplication> results = repository.findAll().stream()
                .filter(application ->
                        application.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                                application.getPhone().replace("+", "")
                                        .contains(query.replace("+", "")) ||
                                application.getPassportData().contains(query))
                .toList();

        log.info("По запросу '{}' найдено {} заявок.", query, results.size());
        return results;
    }

    public List<CreditApplication> getApprovedApplications() {
        return repository.findAll().stream()
                .filter(app -> "Одобрен".equalsIgnoreCase(app.getDecisionStatus()))
                .toList();
    }

    @Transactional
    public CreditApplication makeDecision(Long applicationId) {
        log.info("Принятие решения по заявке с ID: {}", applicationId);

        CreditApplication application = repository.findById(applicationId)
                .orElseThrow(() -> {
                    log.error("Заявка с ID {} не найдена. Принятие решения невозможно.", applicationId);
                    return new EntityNotFoundException("Заявка с ID " + applicationId + " не найдена");
                });

        if (application.getDecisionStatus() != null) {
            log.warn("Решение по заявке с ID {} уже принято: '{}'.", applicationId, application.getDecisionStatus());
            return application; // Возвращаем без изменений
        }

        boolean isApproved = Math.random() > 0.5;
        application.setDecisionStatus(isApproved ? "Одобрен" : "Не одобрен");

        if (isApproved) {
            application.setApprovedTermMonths((int) (Math.random() * 12) + 1);
            application.setApprovedAmount(application.getRequestedAmount() * (0.8 + Math.random() * 0.4));
            log.info("Заявка с ID {} одобрена. Срок: {} месяцев, сумма: {}.",
                    applicationId, application.getApprovedTermMonths(), application.getApprovedAmount());
        } else {
            application.setApprovedAmount(0.0);
            application.setApprovedTermMonths(0);
            log.info("Заявка с ID {} не одобрена.", applicationId);
        }

        repository.save(application);
        return application;
    }

    public CreditApplication getApplicationById(Long id) {
        log.debug("Получение кредитной заявки с ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + id + " не найдена"));
    }

    public List<CreditApplication> getAllApplications() {
        return repository.findAll();
    }

    public void deleteApplication(Long id) {
        log.info("Удаление заявки с ID: {}", id);

        CreditApplication application = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Заявка с ID {} не найдена. Удаление невозможно.", id);
                    return new EntityNotFoundException("Заявка с ID " + id + " не найдена");
                });

        repository.delete(application);
        log.info("Заявка с ID {} успешно удалена.", id);
    }
}