CREATE TABLE IF NOT EXISTS patogeno (
  id int auto_increment NOT NULL ,
  tipo VARCHAR(255) NOT NULL UNIQUE,
  cantidadDeEspecies int auto_increment;
  PRIMARY KEY (id)
)
ENGINE = InnoDB;