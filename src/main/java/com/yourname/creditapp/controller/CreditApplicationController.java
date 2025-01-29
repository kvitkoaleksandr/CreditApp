package com.yourname.creditapp.controller;

import com.yourname.creditapp.dto.CreditApplicationForm;
import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.service.CreditApplicationService;
import com.yourname.creditapp.service.CreditContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class CreditApplicationController {

    private static final Logger log = LoggerFactory.getLogger(CreditApplicationController.class);
    private final CreditApplicationService service;

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/create")
    public String getCreateApplicationPage(Model model) {
        log.debug("Загрузка страницы для создания заявки на кредит");
        model.addAttribute("newCreditApplicationForm", new CreditApplicationForm());
        return "createApplication";
    }

    @PostMapping("/submit")
    public String submitApplication(
            @Valid @ModelAttribute("newCreditApplicationForm") CreditApplicationForm form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "createApplication";
        }

        try {
            CreditApplication savedApplication = service.createApplicationFromForm(form);
            model.addAttribute("createdApplication", savedApplication);
            return "applicationSaved";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Произошла ошибка при сохранении заявки: " + ex.getMessage());
            return "errorPage";
        }
    }

    @GetMapping("/clients")
    public String getAllClientsPage(Model model) {
        List<String> clients = getClients();
        model.addAttribute("clients", clients);
        return "clients";
    }

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

    @GetMapping("/approved/view")
    public String getApprovedApplicationsPage(Model model) {
        List<CreditApplication> approvedApplications = service.getApprovedApplications();
        model.addAttribute("approvedApps", approvedApplications);
        return "approvedApplications";
    }

    private List<String> getClients() {
        return service.getAllApplications().stream()
                .map(CreditApplication::getFullName)
                .toList();
    }
}