package ar.edu.unq.eperdemic.modelo.exception

class PatogenoNoCreadoRunTimeException(patogeno : String) : RuntimeException("No se creó el patógeno: + $patogeno")