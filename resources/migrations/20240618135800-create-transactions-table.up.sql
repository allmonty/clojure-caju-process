CREATE TABLE IF NOT EXISTS transactions(
    id varchar(255) PRIMARY KEY,
    account varchar(255) REFERENCES accounts(id),
    amount bigint,
    merchant_category varchar(255),
    merchant_name varchar(255),
    type varchar(255),
    created_at TIMESTAMP DEFAULT now()
);