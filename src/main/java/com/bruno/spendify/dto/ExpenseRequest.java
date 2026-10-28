package com.bruno.spendify.dto;

import com.bruno.spendify.model.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(

        @NotBlank(message = "description is required")
        @Size(max = 120, message = "description must be at most 120 characters")
        String description,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "amount must have at most 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "date is required")
        @PastOrPresent(message = "date cannot be in the future")
        LocalDate date,

        @NotNull(message = "category is required")
        Category category
) {
}
