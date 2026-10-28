package com.bruno.spendify.repository;

import com.bruno.spendify.dto.CategoryTotal;
import com.bruno.spendify.model.Category;
import com.bruno.spendify.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByOrderByDateDesc();

    List<Expense> findByDateBetweenOrderByDateDesc(LocalDate start, LocalDate end);

    List<Expense> findByCategoryOrderByDateDesc(Category category);

    List<Expense> findByCategoryAndDateBetweenOrderByDateDesc(Category category, LocalDate start, LocalDate end);

    long countByDateBetween(LocalDate start, LocalDate end);

    @Query("""
            select new com.bruno.spendify.dto.CategoryTotal(e.category, sum(e.amount))
            from Expense e
            where e.date between :start and :end
            group by e.category
            order by sum(e.amount) desc
            """)
    List<CategoryTotal> sumByCategory(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
