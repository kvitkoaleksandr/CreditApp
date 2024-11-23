package com.yourname.creditapp.controller;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.service.CreditApplicationService;
import com.yourname.creditapp.service.CreditContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Аннотация @RestController указывает, что это REST-контроллер, возвращающий данные в формате JSON.
@RestController
@RequestMapping("/applications") // Базовый URL для всех запросов к заявкам
@RequiredArgsConstructor
public class CreditApplicationController {

    private final CreditApplicationService service;
    private final CreditContractService contractService;

    @PostMapping("/{id}/decision")
    public CreditApplication makeDecision(@PathVariable Long id) {
        return service.makeDecision(id);
    }

    @GetMapping("/approved")
    public List<CreditApplication> getApprovedApplications() {
        return service.getApprovedApplications();
    }

    @GetMapping("/search")
    public List<CreditApplication> searchApplications(@RequestParam String query) {
        return service.searchApplications(query);
    }

    @GetMapping("/clients")
    public List<String> getAllClients() {
        // Возвращаем список имён всех клиентов
        return service.getAllApplications().stream()
                .map(CreditApplication::getFullName) // Извлекаем только имена
                .toList();
    }

    @PostMapping("/{id}/sign") //Этот эндпоинт будет вызываться,
    // чтобы подписать договор для заявки с указанным id. Пример запроса: POST /applications/1/sign.
    public CreditContract signContract(@PathVariable Long id) {
        // Получаем заявку по ID
        CreditApplication application = service.getApplicationById(id);

        // Подписываем договор через сервис
        return contractService.signContract(application);
    }

    // Метод для создания новой заявки (POST /applications)
    @PostMapping
    public CreditApplication createApplication(@RequestBody CreditApplication application) {
        return service.createApplication(application);
    }

    // Метод для получения заявки по ID (GET /applications/{id})
    @GetMapping("/{id}")
    public CreditApplication getApplication(@PathVariable Long id) {
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
        service.deleteApplication(id);
    }
}

