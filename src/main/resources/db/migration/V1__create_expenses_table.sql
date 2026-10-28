CREATE TABLE expenses (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    description   VARCHAR(120)   NOT NULL,
    amount        DECIMAL(12, 2) NOT NULL,
    expense_date  DATE           NOT NULL,
    category      VARCHAR(20)    NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_expenses_expense_date ON expenses (expense_date);
