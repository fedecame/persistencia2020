package ar.edu.unq.eperdemic.modelo.tipoMutacion

import ar.edu.unq.eperdemic.modelo.Especie

class MutacionDefensaMicroorganismos : TipoMutacion {
    override fun mutarAtributoDeEspecie(especie: Especie) {
        especie.aumentarDefensaContraMicroorganismos()
    }
}