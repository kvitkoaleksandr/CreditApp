package com.yourname.creditapp.controller;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.service.CreditApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreditApplicationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreditApplicationService service;

    @InjectMocks
    private CreditApplicationController controller;

    private CreditApplication testApplication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        testApplication = new CreditApplication();
        testApplication.setId(1L);
        testApplication.setCreatedDate(LocalDate.now());
        testApplication.setFullName("John Doe");
        testApplication.setPassportData("1234 567890");
        testApplication.setMaritalStatus("Single");
        testApplication.setAddress("123 Main St");
        testApplication.setPhone("+1234567890");
        testApplication.setEmploymentDuration("2 years");
        testApplication.setJobTitle("Developer");
        testApplication.setCompanyName("Tech Inc.");
        testApplication.setRequestedAmount(5000.0);
        testApplication.setDecisionStatus("Approved");
        testApplication.setApprovedTermMonths(12);
        testApplication.setApprovedAmount(4800.0);
    }

    @Test
    void getIndexPage_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/applications/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getCreateApplicationPage_ShouldReturnCreateApplicationView() throws Exception {
        performGetRequest("/applications/create", "createApplication",
                "newCreditApplicationForm");
    }

    @Test
    void getAllClientsPage_ShouldReturnClientsView() throws Exception {
        when(service.getAllApplications()).thenReturn(List.of(testApplication));

        mockMvc.perform(get("/applications/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientsView"))
                .andExpect(model().attributeExists("clients"));
    }

    @Test
    void searchApplications_WithQuery_ShouldReturnSearchView() throws Exception {
        when(service.searchApplications("John")).thenReturn(List.of(testApplication));

        mockMvc.perform(get("/applications/search").param("query", "John"))
                .andExpect(status().isOk())
                .andExpect(view().name("searchView"))
                .andExpect(model().attributeExists("searchResults"));
    }

    @Test
    void getApprovedApplicationsPage_ShouldReturnApprovedApplicationsView() throws Exception {
        when(service.getApprovedApplications()).thenReturn(List.of(testApplication));
        performGetRequest("/applications/approved/view", "approvedApplications"
                , "approvedApps");
    }

    private void performGetRequest(String url, String expectedView, String modelAttribute) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(view().name(expectedView))
                .andExpect(model().attributeExists(modelAttribute));
    }
}