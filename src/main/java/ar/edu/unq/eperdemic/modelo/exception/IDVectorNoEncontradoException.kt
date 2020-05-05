package ar.edu.unq.eperdemic.modelo.exception

class IDVectorNoEncontradoException(id : Int) : RuntimeException("El vector de ID $id no fue encontrado")