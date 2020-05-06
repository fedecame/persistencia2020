package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Vector

class Infectado() : EstadoVector {
    override fun infectarse(vector: Vector) {
        //No hace nada
    }

    override fun nombre(): String = "Infectado"

    override fun contagiar(vectorContangia : Vector, vectoresAContagiar: List<Vector>) {
        //vectoresAContagiar.forEach{vectorContagiable -> vectorContagiable.contagiarsePor(vector)}
    }
}