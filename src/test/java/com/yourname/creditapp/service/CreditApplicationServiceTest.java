package com.yourname.creditapp.service;

import com.yourname.creditapp.dto.CreditApplicationForm;
import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.exception.EntityNotFoundException;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.repository.interfaces.CreditApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditApplicationServiceTest {

    @Mock
    private CreditApplicationRepository repository;

    @InjectMocks
    private CreditApplicationService service;

    private CreditApplicationForm form;

    @BeforeEach
    void setUp() {
        form = createCreditApplicationForm("Иван", "Иванов",
                "Иванович", "Москва", "Тверская", "12",
                "+79876543210", "Женат", "36",
                "Менеджер", "ООО Рога и Копыта",
                "1234 567890", 500000.0);
    }

    private CreditApplicationForm createCreditApplicationForm(
            String firstName, String lastName, String middleName,
            String city, String street, String houseNumber, String phone,
            String maritalStatus, String employmentDuration, String jobTitle,
            String companyName, String passportData, double requestedAmount) {

        CreditApplicationForm form = new CreditApplicationForm();
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setMiddleName(middleName);
        form.setCity(city);
        form.setStreet(street);
        form.setHouseNumber(houseNumber);
        form.setPhone(phone);
        form.setMaritalStatus(maritalStatus);
        form.setEmploymentDuration(employmentDuration);
        form.setJobTitle(jobTitle);
        form.setCompanyName(companyName);
        form.setPassportData(passportData);
        form.setRequestedAmount(requestedAmount);
        return form;
    }

    private CreditApplication createCreditApplication(Long id, String fullName, String decisionStatus) {
        CreditApplication application = new CreditApplication();
        application.setId(id);
        application.setFullName(fullName);
        application.setDecisionStatus(decisionStatus);
        return application;
    }

    @Test
    void testProcessApplicationDecision_ApprovedOrNotApproved() {
        CreditApplication application =
                createCreditApplication(1L, "Иванов Иван Иванович", "В ожидании");
        application.setRequestedAmount(500000.0);

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        service.processApplicationDecision(1L);

        assertNotNull(application.getDecisionStatus());
        if ("Одобрен".equals(application.getDecisionStatus())) {
            assertEquals(500000.0, application.getApprovedAmount());
            assertTrue(application.getApprovedTermMonths() > 0 && application.getApprovedTermMonths() <= 12);
        } else {
            assertEquals(0.0, application.getApprovedAmount());
            assertEquals(0, application.getApprovedTermMonths());
        }

        verify(repository).save(application);
    }

    @Test
    void testProcessApplicationDecision_AlreadyDecided() {
        CreditApplication application =
                createCreditApplication(1L, "Иванов Иван Иванович", "Одобрен");

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        service.processApplicationDecision(1L);

        assertEquals("Одобрен", application.getDecisionStatus());
        verify(repository, never()).save(any());
    }

    @Test
    void testProcessApplicationDecision_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.processApplicationDecision(1L));

        assertEquals("Заявка не найдена", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertFormToEntity() {
        CreditApplication application = service.convertFormToEntity(form);

        assertEquals("Иванов Иван Иванович", application.getFullName());
        assertEquals("г. Москва, ул. Тверская, д. 12", application.getAddress());
        assertEquals(500000.0, application.getRequestedAmount());
    }

    @Test
    void testCreateApplicationFromForm_Success() {
        when(repository.findLatestApplicationByClient(anyString(), anyString())).thenReturn(Optional.empty());
        when(repository.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication result = service.createApplicationFromForm(form);

        assertNotNull(result);
        assertEquals("Иванов Иван Иванович", result.getFullName());
        verify(repository).save(result);
    }

    @Test
    void testCreateApplicationFromForm_TooSoon() {
        CreditApplication previousApplication =
                createCreditApplication(1L, "Иванов Иван Иванович", null);
        previousApplication.setCreatedDate(LocalDate.now().minusDays(10));

        when(repository.findLatestApplicationByClient(anyString(), anyString()))
                .thenReturn(Optional.of(previousApplication));

        InvalidActionException exception = assertThrows(InvalidActionException.class,
                () -> service.createApplicationFromForm(form));

        assertTrue(exception.getMessage().contains("До следующей подачи осталось"));
        verify(repository, never()).save(any());
    }

    @Test
    void testSearchApplications_ByFullName() {
        CreditApplication app1 = createCreditApplication(1L, "Иванов Иван Иванович",
                "+79876543210", "1234 567890");
        CreditApplication app2 = createCreditApplication(2L, "Петров Петр Петрович",
                "+79998887766", "9876 543210");

        when(repository.findAll()).thenReturn(List.of(app1, app2));

        List<CreditApplication> result = service.searchApplications("Иванов");

        assertEquals(1, result.size(), "Должна быть найдена одна заявка");
        assertEquals(app1, result.get(0), "Результат должен содержать заявку с именем 'Иванов Иван Иванович'");
    }

    private CreditApplication createCreditApplication(Long id, String fullName, String phone, String passportData) {
        CreditApplication application = new CreditApplication();
        application.setId(id);
        application.setFullName(fullName);
        application.setPhone(phone);
        application.setPassportData(passportData);
        return application;
    }

    @Test
    void testGetApplicationById_Success() {
        CreditApplication application =
                createCreditApplication(1L, "Иванов Иван Иванович", null);

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        CreditApplication result = service.getApplicationById(1L);

        assertEquals("Иванов Иван Иванович", result.getFullName());
        verify(repository).findById(1L);
    }

    @Test
    void testGetApplicationById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getApplicationById(1L));

        assertEquals("Заявка с ID 1 не найдена", exception.getMessage());
    }

    @Test
    void testGetAllApplications() {
        CreditApplication app1 = createCreditApplication(1L, "Иванов Иван Иванович", null);
        CreditApplication app2 = createCreditApplication(2L, "Петров Петр Петрович", null);

        when(repository.findAll()).thenReturn(List.of(app1, app2));

        List<CreditApplication> result = service.getAllApplications();

        assertEquals(2, result.size());
        assertTrue(result.contains(app1));
        assertTrue(result.contains(app2));
    }

    @Test
    void testDeleteApplication_Success() {
        CreditApplication application =
                createCreditApplication(1L, "Иванов Иван Иванович", null);

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        service.deleteApplication(1L);

        verify(repository).delete(application);
    }

    @Test
    void testDeleteApplication_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.deleteApplication(1L));

        assertEquals("Заявка с ID 1 не найдена", exception.getMessage());
    }
}