CREATE TABLE IF NOT EXISTS psychological_tests (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS test_questions (
    id            BIGSERIAL PRIMARY KEY,
    test_id       BIGINT       NOT NULL REFERENCES psychological_tests(id) ON DELETE CASCADE,
    question_text TEXT         NOT NULL,
    order_index   INTEGER      NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP
);

CREATE INDEX idx_test_questions_test_id ON test_questions(test_id);

CREATE TABLE IF NOT EXISTS test_answer_options (
    id                BIGSERIAL PRIMARY KEY,
    question_id       BIGINT       NOT NULL REFERENCES test_questions(id) ON DELETE CASCADE,
    option_text       VARCHAR(500) NOT NULL,
    stress_score      INTEGER      NOT NULL DEFAULT 0,
    motivation_score  INTEGER      NOT NULL DEFAULT 0,
    productivity_score INTEGER     NOT NULL DEFAULT 0,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP
);

CREATE INDEX idx_answer_options_question_id ON test_answer_options(question_id);

CREATE TABLE IF NOT EXISTS user_test_results (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT    NOT NULL,
    test_id            BIGINT    NOT NULL REFERENCES psychological_tests(id),
    stress_level       INTEGER   NOT NULL,
    motivation_level   INTEGER   NOT NULL,
    productivity_level INTEGER   NOT NULL,
    recommendations    TEXT,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP
);

CREATE INDEX idx_user_test_results_user_id ON user_test_results(user_id);
