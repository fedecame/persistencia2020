package ar.edu.unq.eperdemic.modelo.exception

import java.lang.RuntimeException

class IdRepetidoRunTimeException(patogenoId: Int) : RuntimeException("El id $patogenoId se encuentra repetido") {

}
