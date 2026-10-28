package com.bruno.spendify.controller;

import com.bruno.spendify.dto.ExpenseRequest;
import com.bruno.spendify.dto.ExpenseResponse;
import com.bruno.spendify.dto.MonthlySummary;
import com.bruno.spendify.model.Category;
import com.bruno.spendify.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public List<ExpenseResponse> list(@RequestParam(required = false) YearMonth month,
                                      @RequestParam(required = false) Category category) {
        return service.list(month, category);
    }

    @GetMapping("/summary")
    public MonthlySummary summary(@RequestParam(required = false) YearMonth month) {
        return service.summary(month != null ? month : YearMonth.now());
    }

    @GetMapping("/{id}")
    public ExpenseResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@Valid @RequestBody ExpenseRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
