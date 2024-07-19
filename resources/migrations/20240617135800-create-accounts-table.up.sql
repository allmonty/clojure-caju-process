CREATE TABLE IF NOT EXISTS accounts(
    id varchar(255) PRIMARY KEY,
    balance_food bigint,
    balance_meal bigint,
    balance_cash bigint
);