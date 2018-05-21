CREATE TABLE card (
  id        BIGINT                NOT NULL,
  card_name CHARACTER VARYING(32) NOT NULL
);

ALTER TABLE card
  ADD CONSTRAINT pk_card PRIMARY KEY (id);

ALTER TABLE users
  ADD COLUMN card_id BIGINT NULL;
ALTER TABLE ONLY users
  ADD CONSTRAINT fk_users_card FOREIGN KEY (card_id) REFERENCES card (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;

create sequence S_CARD
minvalue 0
start with 1
no cycle;