ALTER TABLE card
  ADD COLUMN created TIMESTAMP NOT NULL DEFAULT now();

CREATE UNIQUE INDEX idx_users_card_id
  ON users (
    card_id ASC
  );

CREATE UNIQUE INDEX idx_card_card_name
  ON card (
    card_name ASC
  );