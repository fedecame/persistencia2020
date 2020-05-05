package ar.edu.unq.eperdemic.modelo.exception

class EstadoNoEncontradoException(unNombre: String) : Exception("El ID $unNombre no es valido")
