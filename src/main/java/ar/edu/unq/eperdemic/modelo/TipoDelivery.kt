package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exception.ClaveRepetidaDeEstadoException
import ar.edu.unq.eperdemic.modelo.exception.EstadoNoEncontradoException

class TipoDelivery {

    private var tipos = mutableMapOf<String, TipoVector>()

    init {
        this.agregarTipo(Animal())
        this.agregarTipo(Humano())
        this.agregarTipo(Insecto())
    }

    fun agregarTipo(nuevoTipo: TipoVector) {
        val nombreTipo = this.format(nuevoTipo::class.java.simpleName)
        this.ifConditionThrow(this.estaEnLaLista(nombreTipo), ClaveRepetidaDeEstadoException(nombreTipo), { tipos.put(nombreTipo.toLowerCase(), nuevoTipo) })
    }

    fun tipo(unNombre: String): TipoVector? {
        return this.ifConditionThrow(!this.estaEnLaLista(unNombre), EstadoNoEncontradoException(unNombre), { tipos.get(unNombre.toLowerCase()) })
    }

    private fun estaEnLaLista(unNombre: String) = tipos.keys.map{this.format(it)}.contains(this.format(unNombre))

    private fun format(word : String) = word.toLowerCase()

    private fun <T> ifConditionThrow(condition: Boolean, e: Exception, bloque: () -> T): T {
        if (condition) {
            throw e
        }
        return bloque()
    }
}
