package com.bruno.spendify.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthlySummary(YearMonth month, BigDecimal total, long count, List<CategoryTotal> byCategory) {
}
