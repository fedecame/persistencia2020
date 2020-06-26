package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

interface EstadoVector {
    fun contagiar(vectorContangia : Vector, vectoresAContagiar: List<Vector>) : List<Pair<Vector, Especie>>
}