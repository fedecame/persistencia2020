package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

class Sano() : EstadoVector {

    override fun contagiar(vectorQueContagia: Vector, vectoresAContagiar: List<Vector>) : List<Pair<Vector, Especie>> {
        return listOf()
    }
}