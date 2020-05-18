package ar.edu.unq.eperdemic.modelo.tipoMutacion

import ar.edu.unq.eperdemic.modelo.Especie

class MutacionLetalidad : TipoMutacion {
    override fun mutarAtributoDeEspecie(especie: Especie) {
        especie.aumentarLetalidad()
    }
}