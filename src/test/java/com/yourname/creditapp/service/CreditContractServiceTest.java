package com.yourname.creditapp.service;

import com.yourname.creditapp.entitiy.CreditApplication;
import com.yourname.creditapp.entitiy.CreditContract;
import com.yourname.creditapp.exception.EntityNotFoundException;
import com.yourname.creditapp.exception.InvalidActionException;
import com.yourname.creditapp.repository.interfaces.CreditContractRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditContractServiceTest {

    @Mock
    private CreditContractRepository contractRepository;

    @InjectMocks
    private CreditContractService service;

    // Вспомогательный метод для создания CreditContract
    private CreditContract createCreditContract(Long id, CreditApplication application, String status) {
        CreditContract contract = new CreditContract();
        contract.setId(id);
        contract.setCreditApplication(application);
        contract.setSigningDate(LocalDate.now());
        contract.setContractStatus(status);
        return contract;
    }

    // Вспомогательный метод для создания CreditApplication
    private CreditApplication createCreditApplication(Long id, String decisionStatus) {
        CreditApplication application = new CreditApplication();
        application.setId(id);
        application.setDecisionStatus(decisionStatus);
        return application;
    }

    @Test
    void testGetSignedContracts_EmptyResult() {
        when(contractRepository.findSignedContracts()).thenReturn(List.of());

        List<CreditContract> result = service.getSignedContracts();

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Результат должен быть пустым, если подписанных договоров нет");

        verify(contractRepository, times(1)).findSignedContracts();
    }

    @Test
    void testSignContract_Success() {
        CreditApplication application = createCreditApplication(1L, "Одобрен");
        CreditContract contract = createCreditContract(1L, application, "Подписан");

        when(contractRepository.findByApplicationId(application.getId())).thenReturn(Optional.empty());
        when(contractRepository.save(any(CreditContract.class))).thenReturn(contract);

        CreditContract result = service.signContract(application);

        assertNotNull(result, "Договор не должен быть null");
        assertEquals(application, result.getCreditApplication(), "Договор должен быть связан с заявкой");
        assertEquals("Подписан", result.getContractStatus(), "Статус договора должен быть 'Подписан'");
        assertEquals(LocalDate.now(), result.getSigningDate(), "Дата подписания должна быть текущей");

        verify(contractRepository).findByApplicationId(application.getId());
        verify(contractRepository).save(any(CreditContract.class));
    }

    @Test
    void testSignContract_NotApprovedApplication() {
        CreditApplication application = createCreditApplication(1L, "Не одобрен");

        InvalidActionException exception = assertThrows(InvalidActionException.class, () -> service.signContract(application));

        assertEquals("Договор можно подписать только для одобренной заявки.", exception.getMessage());

        verify(contractRepository, never()).findByApplicationId(anyLong());
        verify(contractRepository, never()).save(any(CreditContract.class));
    }

    @Test
    void testSignContract_ContractAlreadyExists() {
        CreditApplication application = createCreditApplication(1L, "Одобрен");
        CreditContract existingContract = createCreditContract(1L, application, "Подписан");

        when(contractRepository.findByApplicationId(application.getId())).thenReturn(Optional.of(existingContract));

        InvalidActionException exception = assertThrows(InvalidActionException.class, () -> service.signContract(application));

        assertEquals("Договор для заявки с ID 1 уже существует.", exception.getMessage());

        verify(contractRepository).findByApplicationId(application.getId());
        verify(contractRepository, never()).save(any(CreditContract.class));
    }

    @Test
    void testGetContractById_Success() {
        CreditContract contract = createCreditContract(1L, null, "Подписан");

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        CreditContract result = service.getContractById(1L);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1L, result.getId(), "ID договора должен совпадать");
        assertEquals("Подписан", result.getContractStatus(), "Статус договора должен совпадать");

        verify(contractRepository, times(1)).findById(1L);
    }

    @Test
    void testGetContractById_NotFound() {
        when(contractRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getContractById(1L));

        assertEquals("Договор с ID 1 не найден", exception.getMessage());

        verify(contractRepository, times(1)).findById(1L);
    }
}