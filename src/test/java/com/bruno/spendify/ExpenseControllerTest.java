package com.bruno.spendify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class ExpenseControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void createsValidExpense() throws Exception {
        String json = """
                {"description": "Pharmacy", "amount": 45.90, "date": "2026-10-01", "category": "HEALTH"}
                """;

        mvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.category").value("HEALTH"));
    }

    @Test
    void rejectsInvalidExpense() throws Exception {
        String json = """
                {"description": "", "amount": -10, "date": "2026-10-01", "category": "HEALTH"}
                """;

        mvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.errors.description").isArray())
                .andExpect(jsonPath("$.errors.amount").isArray());
    }

    @Test
    void rejectsUnknownCategory() throws Exception {
        String json = """
                {"description": "Test", "amount": 10, "date": "2026-10-01", "category": "UNKNOWN"}
                """;

        mvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void returns404ForUnknownId() throws Exception {
        mvc.perform(get("/api/expenses/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Expense 9999 not found"));
    }

    @Test
    void summarizesMonthByCategory() throws Exception {
        mvc.perform(get("/api/expenses/summary").param("month", "2026-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2026-10"))
                .andExpect(jsonPath("$.count").value(4))
                .andExpect(jsonPath("$.total").value(2343.40))
                .andExpect(jsonPath("$.byCategory[0].category").value("HOUSING"));
    }
}
