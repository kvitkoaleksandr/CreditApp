package com.yourname.creditapp.controller;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.service.CreditApplicationService;
import com.yourname.creditapp.service.CreditContractService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class CreditContractController {

    private static final Logger log = LoggerFactory.getLogger(CreditContractController.class);
    private final CreditApplicationService applicationService;
    private final CreditContractService contractService;

    @GetMapping("/check-decision")
    public String checkDecision(@RequestParam("id") Long applicationId, Model model) {
        applicationService.processApplicationDecision(applicationId);

        CreditApplication application = applicationService.getApplicationById(applicationId);
        log.info("Решение по заявке: {}", application.getDecisionStatus());

        model.addAttribute("decisionApplication", application);
        return "decisionPage";
    }

    @GetMapping("/{id}/decision")
    public String getDecisionPage(@PathVariable Long id, Model model) {
        log.info("Принятие решения по заявке с ID: {}", id);
        CreditApplication application = applicationService.makeDecision(id);
        model.addAttribute("decisionApplication", application);
        return "decisionPage";
    }

    @GetMapping("/{id}/sign")
    public String signContract(@PathVariable Long id, Model model) {
        log.info("Подписание договора по заявке с ID: {}", id);
        CreditApplication application = applicationService.getApplicationById(id);
        CreditContract contract = contractService.signContract(application);
        model.addAttribute("signedContract", contract);
        return "contractSigned";
    }

    @GetMapping("/contracts/signed/view")
    public String getSignedContractsPage(Model model) {
        List<CreditContract> signedContracts = contractService.getSignedContracts();
        model.addAttribute("signedContracts", signedContracts);
        return "signedContracts";
    }
}