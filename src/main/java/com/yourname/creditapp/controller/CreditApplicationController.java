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

    @GetMapping("/contracts/signed")
    public List<CreditContract> getSignedContracts() {
        log.info("Получен запрос на получение подписанных кредитных договоров.");
        return contractService.getSignedContracts();
    }

    @PostMapping("/{id}/sign-contract")
    public CreditContract signContract(@PathVariable Long id) {
        log.info("Получен запрос на подписание договора по заявке с ID: {}", id);
        CreditApplication application = service.getApplicationById(id);
        if (!"Одобрен".equals(application.getDecisionStatus())) {
            log.warn("Заявка с ID {} не одобрена, подписание невозможно.", id);
            throw new InvalidActionException("Заявка не одобрена. Невозможно подписать договор.");
        }
        return contractService.signContract(application);
    }

    @PostMapping("/{id}/decision")
    public CreditApplication makeDecision(@PathVariable Long id) {
        log.info("Принятие решения по заявке с ID: {}", id);
        return service.makeDecision(id);
    }

    @GetMapping("/approved")
    public List<CreditApplication> getApprovedApplications() {
        return service.getApprovedApplications();
    }

    @GetMapping("/search")
    public List<CreditApplication> searchApplications(@RequestParam String query) {
        log.debug("Поиск заявок с параметром: {}", query);
        return service.searchApplications(query);
    }

    @GetMapping("/clients")
    public List<String> getAllClients() {
        // Возвращаем список имён всех клиентов
        return service.getAllApplications().stream()
                .map(CreditApplication::getFullName) // Извлекаем только имена
                .toList();
    }

    // Метод для создания новой заявки (POST /applications)
    @PostMapping
    public CreditApplication createApplication(@RequestBody CreditApplication application) {
        log.info("Создание новой заявки: {}", application);
        return service.createApplication(application);
    }

    // Метод для получения заявки по ID (GET /applications/{id})
    @GetMapping("/{id}")
    public CreditApplication getApplication(@PathVariable Long id) {
        log.debug("Получение заявки с ID: {}", id);
        return service.getApplicationById(id);
    }

    // Метод для получения всех заявок (GET /applications)
    @GetMapping
    public List<CreditApplication> getAllApplications() {
        return service.getAllApplications();
    }

    // Метод для удаления заявки по ID (DELETE /applications/{id})
    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable Long id) {
        log.info("Удаление заявки с ID: {}", id);
        service.deleteApplication(id);
    }
}