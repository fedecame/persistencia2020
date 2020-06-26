package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

class Infectado() : EstadoVector {

    override fun contagiar(vector : Vector, vectoresAContagiar: List<Vector>) : List<Pair<Vector, Especie>> {
        var vectoresInfectados = listOf<Pair<Vector, Especie>>()
        vectoresAContagiar.forEach{vectorContagiable -> vectoresInfectados = vectoresInfectados + vectorContagiable.contagiarsePor(vector)}
        return vectoresInfectados
    }
}