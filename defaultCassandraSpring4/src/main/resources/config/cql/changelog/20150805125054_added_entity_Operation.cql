
CREATE TABLE IF NOT EXISTS operation (
    id uuid,
    date timestamp,
    description text,
    amount decimal,
    PRIMARY KEY(id)
);
