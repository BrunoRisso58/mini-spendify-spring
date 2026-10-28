package com.bruno.spendify.dto;

import com.bruno.spendify.model.Category;
import com.bruno.spendify.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, String description, BigDecimal amount, LocalDate date, Category category) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getDate(),
                expense.getCategory());
    }
}
