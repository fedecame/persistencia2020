package ar.edu.unq.eperdemic.modelo.tipoMutacion.transformer

import ar.edu.unq.eperdemic.modelo.exception.ClaveRepetidaDeTipoMutacionException
import ar.edu.unq.eperdemic.modelo.exception.TipoMutacionNoEncontradoException
import ar.edu.unq.eperdemic.modelo.tipoMutacion.TipoMutacion
import ar.edu.unq.eperdemic.utility.Delivery

class TipoMutacionDelivery(tiposMutacionList : List<TipoMutacion>) : Delivery<TipoMutacion>(tiposMutacionList) {
    override fun myAddException(nombreTipoMutacion: String): Exception {
        return ClaveRepetidaDeTipoMutacionException(nombreTipoMutacion)
    }

    override fun myGetException(nombreTipoMutacion: String): Exception {
        return TipoMutacionNoEncontradoException(nombreTipoMutacion)
    }
}