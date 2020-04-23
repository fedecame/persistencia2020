CREATE TABLE IF NOT EXISTS patogeno (
  id INT auto_increment NOT NULL,
  tipo VARCHAR(255) NOT NULL UNIQUE,
  cantidadDeEspecies INT,
  PRIMARY KEY (id)
)
ENGINE = InnoDB;

