CREATE TABLE IF NOT EXISTS merchants(
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(255) UNIQUE,
    merchant_category varchar(255)
);