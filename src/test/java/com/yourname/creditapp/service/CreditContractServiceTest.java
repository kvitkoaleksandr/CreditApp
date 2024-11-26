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
    private CreditContractService contractService;

    private CreditApplication application;
    private CreditContract contract;

    @BeforeEach
    void setUp() {
        application = new CreditApplication();
        application.setId(1L);

        contract = new CreditContract();
        contract.setId(1L);
        contract.setCreditApplication(application);
        contract.setSigningDate(LocalDate.now());
        contract.setContractStatus("Подписан");
    }

    @Test
    void testGetSignedContracts() {
        // Подготовка
        when(contractRepository.findSignedContracts()).thenReturn(List.of(contract));

        // Выполнение
        List<CreditContract> signedContracts = contractService.getSignedContracts();

        // Проверки
        assertContractsMatch(signedContracts, List.of(contract));
        verify(contractRepository, times(1)).findSignedContracts();
    }

    @Test
    void testSignContract() {
        // Подготовка
        when(contractRepository.findByApplicationId(1L)).thenReturn(Optional.empty());
        when(contractRepository.save(any(CreditContract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Выполнение
        CreditContract signedContract = contractService.signContract(application);

        // Проверки
        assertEquals("Подписан", signedContract.getContractStatus(), "Статус договора должен быть 'Подписан'");
        assertEquals(application, signedContract.getCreditApplication(), "Договор должен быть связан с заявкой");
        verify(contractRepository).findByApplicationId(1L);
        verify(contractRepository).save(signedContract);
    }

    @Test
    void testSignContractAlreadyExists() {
        // Подготовка
        when(contractRepository.findByApplicationId(1L)).thenReturn(Optional.of(contract));

        // Выполнение и проверка
        InvalidActionException exception = assertThrows(InvalidActionException.class,
                () -> contractService.signContract(application));
        assertTrue(exception.getMessage().contains("уже существует"));

        verify(contractRepository).findByApplicationId(1L);
        verify(contractRepository, never()).save(any(CreditContract.class));
    }

    @Test
    void testGetContractById() {
        // Подготовка
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        // Выполнение
        CreditContract foundContract = contractService.getContractById(1L);

        // Проверки
        assertEquals(contract, foundContract, "Договор не совпадает с ожидаемым");
        verify(contractRepository).findById(1L);
    }

    @Test
    void testGetContractByIdNotFound() {
        // Подготовка
        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        // Выполнение и проверка
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> contractService.getContractById(999L));
        assertTrue(exception.getMessage().contains("не найден"));

        verify(contractRepository).findById(999L);
    }

    // Вспомогательный метод для проверки списка договоров
    private void assertContractsMatch(List<CreditContract> actual, List<CreditContract> expected) {
        assertNotNull(actual, "Список договоров не должен быть null");
        assertEquals(expected.size(), actual.size(), "Размер списка договоров не совпадает");
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i), actual.get(i), "Договор на позиции " + i + " не совпадает");
        }
    }
}