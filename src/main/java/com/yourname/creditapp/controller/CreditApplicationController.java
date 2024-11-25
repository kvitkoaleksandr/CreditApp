package com.yourname.creditapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.service.CreditApplicationService;
import com.yourname.creditapp.service.CreditContractService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class CreditApplicationController {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationController.class);
    private final CreditApplicationService service;
    private final CreditContractService contractService;

    // Главная страница
    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    // Страница для создания заявки
    @GetMapping("/create")
    public String getCreateApplicationPage(Model model) {
        log.debug("Загрузка страницы для создания заявки на кредит");
        model.addAttribute("newCreditApplication", new CreditApplication());
        return "createApplication"; // шаблон createApplication.html
    }

    // Сохранение новой заявки
    // Сохранение новой заявки
    @PostMapping("/create")
    public String createApplication(@ModelAttribute("newCreditApplication") CreditApplication application, Model model) {
        log.info("Получен запрос на создание заявки для клиента: {}", application.getFullName());
        try {
            CreditApplication savedApplication = service.createApplication(application);
            model.addAttribute("createdApplication", savedApplication);
            return "applicationSaved"; // шаблон applicationSaved.html
        } catch (Exception ex) {
            log.error("Ошибка при сохранении заявки: {}", ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Не удалось сохранить заявку. Попробуйте позже.");
            return "createApplication"; // вернуть пользователя на форму с ошибкой
        }
    }

    // Принятие решения по заявке
    @GetMapping("/{id}/decision")
    public String getDecisionPage(@PathVariable Long id, Model model) {
        log.info("Принятие решения по заявке с ID: {}", id);
        CreditApplication application = service.makeDecision(id);
        model.addAttribute("decisionApplication", application);
        return "decisionPage";
    }

    // Подписание кредитного договора
    @GetMapping("/{id}/sign")
    public String signContract(@PathVariable Long id, Model model) {
        log.info("Подписание договора по заявке с ID: {}", id);
        CreditApplication application = service.getApplicationById(id);
        CreditContract contract = contractService.signContract(application);
        model.addAttribute("signedContract", contract);
        return "contractSigned";
    }

    // Вывод списка всех клиентов
    @GetMapping("/clients")
    public String getAllClientsPage(Model model) {
        List<String> clients = service.getAllApplications().stream()
                .map(CreditApplication::getFullName)
                .toList();
        model.addAttribute("clients", clients);
        return "clients";
    }

    // Поиск клиента по параметрам
    @GetMapping("/search")
    public String searchApplications(@RequestParam(required = false) String query, Model model) {
        if (query == null || query.isBlank()) {
            model.addAttribute("searchResults", List.of());
        } else {
            log.info("Поиск клиента с параметром: {}", query);
            List<CreditApplication> results = service.searchApplications(query);
            model.addAttribute("searchResults", results);
        }
        return "search";
    }

    // Вывод списка одобренных заявок
    @GetMapping("/approved/view")
    public String getApprovedApplicationsPage(Model model) {
        List<CreditApplication> approvedApplications = service.getApprovedApplications();
        model.addAttribute("approvedApps", approvedApplications);
        return "approvedApplications";
    }

    // Вывод подписанных договоров
    @GetMapping("/contracts/signed/view")
    public String getSignedContractsPage(Model model) {
        List<CreditContract> signedContracts = contractService.getSignedContracts();
        model.addAttribute("signedContracts", signedContracts);
        return "signedContracts";
    }

    // Вывод всех заявок (если необходимо)
    @GetMapping("/view")
    public String getAllApplicationsPage(Model model) {
        List<CreditApplication> applications = service.getAllApplications();
        model.addAttribute("creditApplications", applications);
        return "creditApplications";
    }
}