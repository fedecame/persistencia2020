package ar.edu.unq.eperdemic.modelo.exception

class ClaveRepetidaDeEstadoException(key : String) : Exception("La clave $key ya esta en uso")