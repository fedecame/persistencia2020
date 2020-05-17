package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Vector

class Sano() : EstadoVector {

    val nombre = "Sano"

    override fun contagiar(vectorQueContagia: Vector, vectoresAContagiar: List<Vector>) {
        //No hace nada
    }
}