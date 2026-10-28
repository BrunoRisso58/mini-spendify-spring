package com.bruno.spendify.dto;

import com.bruno.spendify.model.Category;

import java.math.BigDecimal;

public record CategoryTotal(Category category, BigDecimal total) {
}
