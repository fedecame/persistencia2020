CREATE TABLE IF NOT EXISTS patogeno (
  id INT auto_increment NOT NULL ,
  tipo VARCHAR(255) NOT NULL,
  cantidadDeEspecies INT, /*Saque el autoincremental de cantidadDeEspecies*/
  PRIMARY KEY (id)
)
ENGINE = InnoDB;

