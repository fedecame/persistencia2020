package ar.edu.unq.eperdemic.modelo.exception

class PatogenoNotFoundRunTimeException(patogeno_id : Int) : RuntimeException("El patógeno de id $patogeno_id no fue encontrado")