CREATE TABLE weekend_days (
  id BIGINT NOT NULL ,
  month_id BIGINT NOT NULL ,
  weekend_day INT  NOT NULL ,
  weekend_name VARCHAR(255) NOT NULL
);

ALTER TABLE weekend_days ADD CONSTRAINT fk_weekend_days_month FOREIGN KEY (month_id) REFERENCES month (id);

CREATE SEQUENCE s_weekend_days START WITH 1 MINVALUE 0 NO CYCLE;
