DROP TABLE IF EXISTS schedule;
CREATE TABLE schedule (
  idOperacao SERIAL,
  indiceTransacao INTEGER,
  operacao VARCHAR(10),
  itemDado VARCHAR(10),
  timestampj VARCHAR(15),
  flag INTEGER,
  CONSTRAINT pk_constraint PRIMARY KEY (idOperacao)
);
DROP TABLE IF EXISTS scheduleout;
CREATE TABLE scheduleout (
  idOperacao SERIAL,
  indiceTransacao INTEGER,
  operacao VARCHAR(10),
  itemDado VARCHAR(10),
  timestampj VARCHAR(15)
)