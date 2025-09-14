CREATE TABLE tasks
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(128) NOT NULL,
    description  VARCHAR(2048),
    category     VARCHAR(32) NOT NULL,
    priority     VARCHAR(6),
    deadline     DATE,
    completed    BOOLEAN     NOT NULL DEFAULT FALSE
);