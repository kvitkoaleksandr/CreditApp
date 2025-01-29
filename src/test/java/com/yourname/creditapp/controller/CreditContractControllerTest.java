package com.yourname.creditapp.controller;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.service.CreditApplicationService;
import com.yourname.creditapp.service.CreditContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CreditContractControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreditApplicationService applicationService;

    @Mock
    private CreditContractService contractService;

    @Mock
    private Model model;

    @InjectMocks
    private CreditContractController controller;

    private CreditApplication application;
    private CreditContract contract;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        application = new CreditApplication();
        application.setId(1L);
        application.setDecisionStatus("Одобрен");

        contract = new CreditContract();
        contract.setCreditApplication(application);
        contract.setContractStatus("Подписан");
    }

    @Test
    void checkDecision_ShouldReturnDecisionPage() throws Exception {
        Long applicationId = application.getId();

        when(applicationService.getApplicationById(applicationId)).thenReturn(application);

        mockMvc.perform(get("/applications/check-decision").param("id", applicationId.toString()))
                .andExpectAll(
                        commonExpectations("decisionPage", "decisionApplication")
                );

        verify(applicationService, times(1)).processApplicationDecision(applicationId);
        verify(applicationService, times(1)).getApplicationById(applicationId);
    }

    @Test
    void getDecisionPage_ShouldReturnDecisionPage() throws Exception {
        Long applicationId = application.getId();

        when(applicationService.makeDecision(applicationId)).thenReturn(application);

        mockMvc.perform(get("/applications/{id}/decision", applicationId))
                .andExpectAll(
                        commonExpectations("decisionPage", "decisionApplication")
                );

        verify(applicationService, times(1)).makeDecision(applicationId);
    }

    @Test
    void signContract_ShouldReturnContractSignedPage() throws Exception {
        Long applicationId = application.getId();

        when(applicationService.getApplicationById(applicationId)).thenReturn(application);
        when(contractService.signContract(application)).thenReturn(contract);

        mockMvc.perform(get("/applications/{id}/sign", applicationId))
                .andExpectAll(
                        commonExpectations("contractSigned", "signedContract")
                );

        verify(applicationService, times(1)).getApplicationById(applicationId);
        verify(contractService, times(1)).signContract(application);
    }

    @Test
    void getSignedContractsPage_ShouldReturnSignedContractsPage() throws Exception {
        List<CreditContract> signedContracts = List.of(contract, contract);

        when(contractService.getSignedContracts()).thenReturn(signedContracts);

        mockMvc.perform(get("/applications/contracts/signed/view"))
                .andExpectAll(
                        commonExpectations("signedContracts", "signedContracts")
                );

        verify(contractService, times(1)).getSignedContracts();
    }

    private static org.springframework.test.web.servlet.ResultMatcher[] commonExpectations(String viewName
            , String modelAttribute) {
        return new org.springframework.test.web.servlet.ResultMatcher[]{
                status().isOk(),
                view().name(viewName),
                model().attributeExists(modelAttribute)
        };
    }
}