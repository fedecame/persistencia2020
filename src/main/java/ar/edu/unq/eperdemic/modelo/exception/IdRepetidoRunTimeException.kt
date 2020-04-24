package ar.edu.unq.eperdemic.modelo.exception

class IdRepetidoRunTimeException(patogenoId: Int) : RuntimeException("El id $patogenoId se encuentra repetido")
