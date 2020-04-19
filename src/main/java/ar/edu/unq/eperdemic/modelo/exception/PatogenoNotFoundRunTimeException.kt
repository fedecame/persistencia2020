package ar.edu.unq.eperdemic.modelo.exception

import java.lang.RuntimeException

class PatogenoNotFoundRunTimeException(patogeno_tipo : String) : RuntimeException("El patógeno $patogeno_tipo no fue encontrado")