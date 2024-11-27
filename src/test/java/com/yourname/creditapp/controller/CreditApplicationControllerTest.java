package com.yourname.creditapp.controller;


import com.yourname.creditapp.dto.CreditApplicationForm;
import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.service.CreditApplicationService;
import com.yourname.creditapp.service.CreditContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(MockitoExtension.class)
class CreditApplicationControllerTest {

    @Mock
    private CreditApplicationService service;

    @Mock
    private CreditContractService contractService;

    @InjectMocks
    private CreditApplicationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCheckDecision() throws Exception {
        CreditApplication application = new CreditApplication();
        application.setId(1L);
        application.setDecisionStatus("Одобрен");

        when(service.getApplicationById(1L)).thenReturn(application);

        mockMvc.perform(get("/applications/check-decision")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("decisionPage"))
                .andExpect(model().attribute("decisionApplication", application));

        verify(service).processApplicationDecision(1L);
        verify(service).getApplicationById(1L);
    }

    @Test
    void testSubmitApplication_ValidForm() throws Exception {
        CreditApplication application = new CreditApplication();
        application.setId(1L);
        application.setFullName("Иванов Иван Иванович");

        when(service.createApplicationFromForm(any(CreditApplicationForm.class))).thenReturn(application);

        mockMvc.perform(post("/applications/submit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Иван")
                        .param("lastName", "Иванов")
                        .param("middleName", "Иванович")
                        .param("requestedAmount", "500000"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicationSaved"))
                .andExpect(model().attribute("createdApplication", application));

        verify(service).createApplicationFromForm(any(CreditApplicationForm.class));
    }

    @Test
    void testGetApprovedApplications() throws Exception {
        List<CreditApplication> approvedApplications = List.of(new CreditApplication());
        when(service.getApprovedApplications()).thenReturn(approvedApplications);

        mockMvc.perform(get("/applications/approved"))
                .andExpect(status().isOk())
                .andExpect(view().name("approvedApplications"))
                .andExpect(model().attribute("approvedApplications", approvedApplications));

        verify(service).getApprovedApplications();
    }

    @Test
    void testViewClients() throws Exception {
        when(service.getAllApplications()).thenReturn(List.of(new CreditApplication()));

        mockMvc.perform(get("/applications/clients/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients"))
                .andExpect(model().attributeExists("clients"));

        verify(service).getAllApplications();
    }

    @Test
    void testGetIndexPage() throws Exception {
        mockMvc.perform(get("/applications/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testGetCreateApplicationPage() throws Exception {
        mockMvc.perform(get("/applications/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createApplication"))
                .andExpect(model().attributeExists("newCreditApplicationForm"));
    }

    @Test
    void testCreateApplication() throws Exception {
        CreditApplication application = new CreditApplication();
        when(service.createApplicationFromForm(any(CreditApplicationForm.class))).thenReturn(application);

        mockMvc.perform(post("/applications/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Иван")
                        .param("lastName", "Иванов")
                        .param("requestedAmount", "500000"))
                .andExpect(status().isOk())
                .andExpect(view().name("applicationSaved"))
                .andExpect(model().attributeExists("createdApplication"));

        verify(service).createApplicationFromForm(any(CreditApplicationForm.class));
    }

    @Test
    void testGetDecisionPage() throws Exception {
        CreditApplication application = new CreditApplication();
        application.setId(1L);
        when(service.makeDecision(1L)).thenReturn(application);

        mockMvc.perform(get("/applications/1/decision"))
                .andExpect(status().isOk())
                .andExpect(view().name("decisionPage"))
                .andExpect(model().attribute("decisionApplication", application));

        verify(service).makeDecision(1L);
    }

    @Test
    void testSignContract() throws Exception {
        CreditApplication application = new CreditApplication();
        CreditContract contract = new CreditContract();
        when(service.getApplicationById(1L)).thenReturn(application);
        when(contractService.signContract(application)).thenReturn(contract);

        mockMvc.perform(get("/applications/1/sign"))
                .andExpect(status().isOk())
                .andExpect(view().name("contractSigned"))
                .andExpect(model().attribute("signedContract", contract));

        verify(service).getApplicationById(1L);
        verify(contractService).signContract(application);
    }

    @Test
    void testSearchApplications() throws Exception {
        when(service.searchApplications("Иванов")).thenReturn(List.of(new CreditApplication()));

        mockMvc.perform(get("/applications/search")
                        .param("query", "Иванов"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("searchResults"));

        verify(service).searchApplications("Иванов");
    }

    @Test
    void testGetApprovedApplicationsPage() throws Exception {
        when(service.getApprovedApplications()).thenReturn(List.of(new CreditApplication()));

        mockMvc.perform(get("/applications/approved/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("approvedApplications"))
                .andExpect(model().attributeExists("approvedApps"));

        verify(service).getApprovedApplications();
    }

    @Test
    void testGetSignedContractsPage() throws Exception {
        when(contractService.getSignedContracts()).thenReturn(List.of(new CreditContract()));

        mockMvc.perform(get("/applications/contracts/signed/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("signedContracts"))
                .andExpect(model().attributeExists("signedContracts"));

        verify(contractService).getSignedContracts();
    }

    @Test
    void testGetAllApplicationsPage() throws Exception {
        when(service.getAllApplications()).thenReturn(List.of(new CreditApplication()));

        mockMvc.perform(get("/applications/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("creditApplications"))
                .andExpect(model().attributeExists("creditApplications"));

        verify(service).getAllApplications();
    }
}