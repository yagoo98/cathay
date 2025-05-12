create table COIN
(
    ID      INTEGER auto_increment primary key,
    NAME    CHARACTER VARYING,
    NAMEZH  CHARACTER VARYING,
    RATE    NUMERIC(20, 4),
    CREATED TIMESTAMP,
    UPDATED TIMESTAMP
);

