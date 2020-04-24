CREATE TABLE IF NOT EXISTS patogeno (
  id INT auto_increment NOT NULL,
  tipo VARCHAR(255) NOT NULL UNIQUE,
  cantidadDeEspecies INT NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = InnoDB;