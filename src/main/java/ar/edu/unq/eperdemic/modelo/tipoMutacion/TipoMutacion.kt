package ar.edu.unq.eperdemic.modelo.tipoMutacion

import ar.edu.unq.eperdemic.modelo.Especie

interface TipoMutacion {
    fun mutarAtributoDeEspecie(especie: Especie)
}