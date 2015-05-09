-- Table: user_login

-- DROP TABLE user_login;

CREATE TABLE user_login
(
  hash text NOT NULL,
  username text NOT NULL,
  email text,
  salt text NOT NULL,
  CONSTRAINT cx_username_unique UNIQUE (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_login
  OWNER TO postgres;
