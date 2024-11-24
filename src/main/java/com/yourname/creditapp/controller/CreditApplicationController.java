package com.yourname.creditapp.controller;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.service.CreditApplicationService;
import com.yourname.creditapp.service.CreditContractService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Аннотация @RestController указывает, что это REST-контроллер, возвращающий данные в формате JSON.
@RestController
@RequestMapping("/applications") // Базовый URL для всех запросов к заявкам
@RequiredArgsConstructor
public class CreditApplicationController {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationController.class); // Создаём логгер
    private final CreditApplicationService service;
    private final CreditContractService contractService;

    // Получение всех подписанных кредитных договоров
    @GetMapping("/contracts/signed")
    public List<CreditContract> getSignedContracts() {
        return contractService.getSignedContracts();
    }

    // Подписание кредитного договора по ID заявки
    @PostMapping("/{id}/sign-contract")
    public CreditContract signContract(@PathVariable Long id) {
        log.info("Запрос на подписание договора по заявке с ID: {}", id);

        // Получаем заявку по ID
        CreditApplication application = service.getApplicationById(id);
        if (!"Одобрен".equals(application.getDecisionStatus())) {
            log.warn("Попытка подписать договор для не одобренной заявки с ID: {}", id);
            throw new InvalidActionException("Заявка не одобрена. Подписание невозможно.");
        }

        // Подписываем договор
        CreditContract contract = contractService.signContract(application);
        log.info("Договор по заявке с ID {} успешно подписан.", id);
        return contract;
    }

    // Принятие решения по заявке
    @PostMapping("/{id}/decision")
    public CreditApplication makeDecision(@PathVariable Long id) {
        log.info("Принятие решения по заявке с ID: {}", id);
        return service.makeDecision(id);
    }

    // Получение списка всех одобренных заявок
    @GetMapping("/approved")
    public List<CreditApplication> getApprovedApplications() {
        return service.getApprovedApplications();
    }

    // Поиск заявок по параметру (ФИО, телефон или паспортные данные)
    @GetMapping("/search")
    public List<CreditApplication> searchApplications(@RequestParam String query) {
        log.info("Поиск заявок с запросом: {}", query);
        List<CreditApplication> results = service.searchApplications(query);
        log.info("По запросу '{}' найдено {} заявок.", query, results.size());
        return results;
    }

    // Получение списка всех клиентов
    @GetMapping("/clients")
    public List<String> getAllClients() {
        // Возвращаем список имён всех клиентов
        return service.getAllApplications().stream()
                .map(CreditApplication::getFullName) // Извлекаем только имена
                .toList();
    }

    // Создание новой заявки (POST /applications)
    @PostMapping
    public CreditApplication createApplication(@RequestBody CreditApplication application) {
        log.info("Создание новой заявки для клиента: {}", application.getFullName());
        return service.createApplication(application);
    }

    // Получение заявки по ID (GET /applications/{id})
    @GetMapping("/{id}")
    public CreditApplication getApplication(@PathVariable Long id) {
        return service.getApplicationById(id);
    }

    // Получение всех заявок (GET /applications)
    @GetMapping
    public List<CreditApplication> getAllApplications() {
        return service.getAllApplications();
    }

    // Удаление заявки по ID (DELETE /applications/{id})
    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable Long id) {
        log.info("Удаление заявки с ID: {}", id);
        service.deleteApplication(id);
        log.info("Заявка с ID {} успешно удалена.", id);
    }
}