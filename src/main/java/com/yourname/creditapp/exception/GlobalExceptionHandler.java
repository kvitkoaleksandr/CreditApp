package com.yourname.creditapp.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработчик для EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "errorPage"; // Убедитесь, что errorPage.html существует
    }

    // Обработчик для InvalidActionException
    @ExceptionHandler(InvalidActionException.class)
    public String handleInvalidActionException(InvalidActionException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "errorPage"; // Убедитесь, что errorPage.html существует
    }

    // Обработчик для остальных исключений
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка: " + ex.getMessage());
        return "errorPage"; // Убедитесь, что errorPage.html существует
    }
}