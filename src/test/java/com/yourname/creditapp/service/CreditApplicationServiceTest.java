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
        form = createCreditApplicationForm("Иван", "Иванов", "Иванович", "+79876543210", 500000.0);
    }

    private CreditApplicationForm createCreditApplicationForm(String firstName, String lastName, String middleName, String phone, Double amount) {
        CreditApplicationForm form = new CreditApplicationForm();
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setMiddleName(middleName);
        form.setCity("Москва");
        form.setStreet("Тверская");
        form.setHouseNumber("12");
        form.setPhone(phone);
        form.setMaritalStatus("Женат");
        form.setEmploymentDuration("36");
        form.setJobTitle("Менеджер");
        form.setCompanyName("ООО Рога и Копыта");
        form.setPassportData("1234 567890");
        form.setRequestedAmount(amount);
        return form;
    }

    private CreditApplication createCreditApplication(Long id, String fullName, String phone, String decisionStatus) {
        CreditApplication application = new CreditApplication();
        application.setId(id);
        application.setFullName(fullName);
        application.setPhone(phone);
        application.setDecisionStatus(decisionStatus);
        return application;
    }

    @Test
    void testCreateApplicationFromFormSuccess() {
        when(repository.findLatestApplicationByClient(anyString(), anyString())).thenReturn(Optional.empty());
        when(repository.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication result = service.createApplicationFromForm(form);

        assertNotNull(result);
        assertEquals("Иванов Иван Иванович", result.getFullName());
        verify(repository).save(any(CreditApplication.class));
    }

    @Test
    void testCreateApplicationFromFormTooSoon() {
        CreditApplication recentApplication = createCreditApplication(null, "Иванов Иван Иванович", "+79876543210", null);
        recentApplication.setCreatedDate(LocalDate.now().minusDays(10));

        when(repository.findLatestApplicationByClient(anyString(), anyString())).thenReturn(Optional.of(recentApplication));

        InvalidActionException exception = assertThrows(InvalidActionException.class,
                () -> service.createApplicationFromForm(form));

        assertTrue(exception.getMessage().contains("До следующей подачи осталось 18 дней"));
        verify(repository, never()).save(any());
    }

    @Test
    void testProcessApplicationDecisionSuccess() {
        CreditApplication application = createCreditApplication(1L, "Иванов Иван", "+79876543210", null);
        application.setRequestedAmount(500000.0);

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        service.processApplicationDecision(1L);

        assertNotNull(application.getDecisionStatus());
        if ("Одобрен".equals(application.getDecisionStatus())) {
            assertEquals(500000.0, application.getApprovedAmount());
            assertTrue(application.getApprovedTermMonths() > 0);
        } else if ("Не одобрен".equals(application.getDecisionStatus())) {
            assertEquals(0.0, application.getApprovedAmount());
            assertEquals(0, application.getApprovedTermMonths());
        }

        verify(repository).save(application);
    }

    @Test
    void testGetApplicationByIdSuccess() {
        CreditApplication application = createCreditApplication(1L, "Иванов Иван Иванович", "+79876543210", null);

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        CreditApplication result = service.getApplicationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иванов Иван Иванович", result.getFullName());
        verify(repository).findById(1L);
    }

    @Test
    void testGetApplicationByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getApplicationById(1L));
        verify(repository).findById(1L);
    }

    @Test
    void testGetAllApplications() {
        CreditApplication app1 = createCreditApplication(1L, "Иванов Иван", "+79876543210", "Одобрен");
        CreditApplication app2 = createCreditApplication(2L, "Петров Петр", "+79998887766", "Не одобрен");

        when(repository.findAll()).thenReturn(List.of(app1, app2));

        List<CreditApplication> result = service.getAllApplications();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void testDeleteApplicationSuccess() {
        CreditApplication application = createCreditApplication(1L, "Иванов Иван", "+79876543210", null);

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        service.deleteApplication(1L);

        verify(repository).delete(application);
        verify(repository).findById(1L);
    }

    @Test
    void testDeleteApplicationNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteApplication(1L));
        verify(repository, never()).delete(any());
    }



    @Test
    void testGetApprovedApplications() {
        CreditApplication approved = createCreditApplication(1L, "Иванов Иван", "+79876543210", "Одобрен");
        CreditApplication notApproved = createCreditApplication(2L, "Петров Петр", "+79998887766", "Не одобрен");

        when(repository.findAll()).thenReturn(List.of(approved, notApproved));

        List<CreditApplication> result = service.getApprovedApplications();

        assertEquals(1, result.size());
        assertTrue(result.contains(approved));
        assertFalse(result.contains(notApproved));
        verify(repository).findAll();
    }

    @Test
    void testMakeDecisionSuccess() {
        CreditApplication application = createCreditApplication(1L, "Иванов Иван", "+79876543210", null);
        application.setRequestedAmount(500000.0);

        when(repository.findById(1L)).thenReturn(Optional.of(application));

        CreditApplication result = service.makeDecision(1L);

        assertNotNull(result.getDecisionStatus(), "Решение должно быть принято");
        if ("Одобрен".equals(result.getDecisionStatus())) {
            assertNotNull(result.getApprovedAmount(), "Одобренная сумма не должна быть null");
            assertTrue(result.getApprovedAmount() >= 400000.0 && result.getApprovedAmount() <= 600000.0,
                    "Одобренная сумма должна быть в диапазоне от 400000 до 600000");
            assertNotNull(result.getApprovedTermMonths(), "Срок кредита не должен быть null");
            assertTrue(result.getApprovedTermMonths() > 0, "Срок кредита должен быть больше 0");
        } else {
            assertNull(result.getApprovedAmount(), "Для отклонённых заявок сумма должна быть null");
            assertNull(result.getApprovedTermMonths(), "Для отклонённых заявок срок должен быть null");
        }

        verify(repository, times(1)).save(application);
    }

    @Test
    void testSearchApplications() {
        CreditApplication app1 = createCreditApplication(1L, "Иванов Иван Иванович", "+79876543210", null);
        app1.setPassportData("1234 567890");
        CreditApplication app2 = createCreditApplication(2L, "Петров Петр", "+79998887766", null);
        app2.setPassportData("9876 543210"); // Заполняем passportData, чтобы избежать NullPointerException

        when(repository.findAll()).thenReturn(List.of(app1, app2));

        // Выполняем поиск
        List<CreditApplication> results = service.searchApplications("Иванов");

        // Проверяем результаты
        assertEquals(1, results.size(), "Должна быть найдена одна заявка");
        assertEquals("Иванов Иван Иванович", results.get(0).getFullName(), "Имя клиента должно совпадать");

        verify(repository).findAll();
    }
}