package com.bruno.spendify.service;

import com.bruno.spendify.dto.CategoryTotal;
import com.bruno.spendify.dto.ExpenseRequest;
import com.bruno.spendify.dto.ExpenseResponse;
import com.bruno.spendify.dto.MonthlySummary;
import com.bruno.spendify.exception.ResourceNotFoundException;
import com.bruno.spendify.model.Category;
import com.bruno.spendify.model.Expense;
import com.bruno.spendify.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(YearMonth month, Category category) {
        List<Expense> expenses;

        if (month != null && category != null) {
            expenses = repository.findByCategoryAndDateBetweenOrderByDateDesc(category, month.atDay(1), month.atEndOfMonth());
        } else if (month != null) {
            expenses = repository.findByDateBetweenOrderByDateDesc(month.atDay(1), month.atEndOfMonth());
        } else if (category != null) {
            expenses = repository.findByCategoryOrderByDateDesc(category);
        } else {
            expenses = repository.findAllByOrderByDateDesc();
        }

        return expenses.stream().map(ExpenseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return ExpenseResponse.from(getExpense(id));
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        Expense expense = new Expense(request.description(), request.amount(), request.date(), request.category());
        return ExpenseResponse.from(repository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = getExpense(id);
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setDate(request.date());
        expense.setCategory(request.category());
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getExpense(id));
    }

    @Transactional(readOnly = true)
    public MonthlySummary summary(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<CategoryTotal> byCategory = repository.sumByCategory(start, end);

        BigDecimal total = byCategory.stream()
                .map(CategoryTotal::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = repository.countByDateBetween(start, end);

        return new MonthlySummary(month, total, count, byCategory);
    }

    private Expense getExpense(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense " + id + " not found"));
    }
}
