CREATE TABLE account
(
    pk_id   BIGINT AUTO_INCREMENT NOT NULL,
    iban    VARCHAR(255)          NULL,
    balance DOUBLE                NULL,
    owner   VARCHAR(255)          NULL,
    CONSTRAINT pk_account PRIMARY KEY (pk_id)
);

INSERT INTO `account` (`pk_id`, `iban`, `balance`, `owner`) VALUES (1, 'Alice-123', 10, 'Adam');
INSERT INTO `account` (`pk_id`, `iban`, `balance`, `owner`) VALUES (2, 'Bob-456', 0, 'Bob');
