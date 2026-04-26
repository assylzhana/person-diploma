CREATE TABLE IF NOT EXISTS goals (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    category            VARCHAR(50)  NOT NULL,
    status              VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    period_type         VARCHAR(50)  NOT NULL,
    progress_percentage INTEGER      NOT NULL DEFAULT 0,
    deadline            DATE         NOT NULL,
    deadline_notified   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_goals_user_id ON goals(user_id);
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_deadline ON goals(deadline);
CREATE INDEX idx_goals_category ON goals(category);
