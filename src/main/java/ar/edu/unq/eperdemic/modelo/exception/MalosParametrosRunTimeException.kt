package ar.edu.unq.eperdemic.modelo.exception

class MalosParametrosRunTimeException(aNumber: Double, otherNumber: Double) : RuntimeException("El parametro de la Izquierda *aNumber*: $aNumber debe ser **MENOR** que el parametro de la Derecha *otherNumber**: $otherNumber")
