CREATE TABLE IF NOT EXISTS monthly_finances (
    id           BIGSERIAL    PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    year         INTEGER      NOT NULL,
    month        INTEGER      NOT NULL,
    base_income  NUMERIC(15,2) NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP,
    UNIQUE (user_id, year, month)
);

CREATE INDEX idx_monthly_finances_user_id ON monthly_finances(user_id);
CREATE INDEX idx_monthly_finances_year_month ON monthly_finances(year, month);

CREATE TABLE IF NOT EXISTS incomes (
    id                  BIGSERIAL    PRIMARY KEY,
    monthly_finance_id  BIGINT       NOT NULL REFERENCES monthly_finances(id) ON DELETE CASCADE,
    amount              NUMERIC(15,2) NOT NULL,
    type                VARCHAR(50)  NOT NULL,
    description         VARCHAR(500),
    date                DATE         NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_incomes_monthly_finance_id ON incomes(monthly_finance_id);

CREATE TABLE IF NOT EXISTS expenses (
    id                  BIGSERIAL    PRIMARY KEY,
    monthly_finance_id  BIGINT       NOT NULL REFERENCES monthly_finances(id) ON DELETE CASCADE,
    amount              NUMERIC(15,2) NOT NULL,
    category            VARCHAR(50)  NOT NULL,
    description         VARCHAR(500),
    date                DATE         NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_expenses_monthly_finance_id ON expenses(monthly_finance_id);
CREATE INDEX idx_expenses_category ON expenses(category);
