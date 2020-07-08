package ar.edu.unq.eperdemic.estado.transformer

import ar.edu.unq.eperdemic.estado.EstadoVector
import ar.edu.unq.eperdemic.modelo.exception.ClaveRepetidaDeEstadoException
import ar.edu.unq.eperdemic.modelo.exception.EstadoNoEncontradoException
import ar.edu.unq.eperdemic.utility.Delivery


class EstadoDelivery(estadosList : List<EstadoVector>) : Delivery<EstadoVector>(estadosList) {
    override fun myAddException(nombreTipo : String) = ClaveRepetidaDeEstadoException(nombreTipo)
    override fun myGetException(nombreTipo : String) = EstadoNoEncontradoException(nombreTipo)
}
