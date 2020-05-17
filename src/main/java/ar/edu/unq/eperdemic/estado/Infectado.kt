package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Vector

class Infectado() : EstadoVector {

    override fun estaInfectado(): Boolean = true

    override fun contagiar(vector : Vector, vectoresAContagiar: List<Vector>) {
        vectoresAContagiar.forEach{vectorContagiable -> vectorContagiable.contagiarsePor(vector)}
    }
}