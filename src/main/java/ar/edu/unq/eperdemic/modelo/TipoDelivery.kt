package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.ClaveRepetidaDeEstadoException
import ar.edu.unq.eperdemic.modelo.exception.EstadoNoEncontradoException

class TipoDelivery {

    private var tipos = mutableMapOf<String, TipoVector>()

    init {
        this.agregarTipo(TipoAnimal())
        this.agregarTipo(TipoHumano())
        this.agregarTipo(TipoInsecto())
    }

    fun agregarTipo(nuevoTipo: TipoVector) {
//        this.ifConditionThrow(this.estaEnLaLista(nombreTipo), ClaveRepetidaDeEstadoException(nombreTipo), { tipos.put(nombreTipo, nuevoTipo) })
        tipos.put(nuevoTipo::class.java.simpleName, nuevoTipo)
    }

    fun tipo(unNombre: String): TipoVector? {
        //return this.ifConditionThrow(!this.estaEnLaLista(unNombre), EstadoNoEncontradoException(unNombre), { tipos.get(this.format(unNombre)) })
        return tipos.get(unNombre)
    }

    private fun estaEnLaLista(unNombre: String) = tipos.keys.contains(unNombre)

    private fun <T> ifConditionThrow(condition: Boolean, e: Exception, bloque: () -> T): T {
        if (condition) {
            throw e
        }
        return bloque()
    }
}
