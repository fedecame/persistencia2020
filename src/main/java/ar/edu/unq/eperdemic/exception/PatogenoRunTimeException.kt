package ar.edu.unq.eperdemic.exception

class PatogenoRunTimeException(patogeno : String) : RuntimeException("No se insertó el patógeno: + $patogeno")