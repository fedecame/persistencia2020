package ar.edu.unq.eperdemic.modelo.exception

class IDMutacionNoEncontradoException(id : Int) : RuntimeException("La mutacion de ID $id no fue encontrada")