package com.bruno.spendify.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate date;

    // Stored as VARCHAR to match the Flyway schema; Hibernate would otherwise expect a native ENUM column on MySQL.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private Category category;

    protected Expense() {
    }

    public Expense(String description, BigDecimal amount, LocalDate date, Category category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public Long getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
