package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Vector

class Sano() : EstadoVector {

    override fun estaInfectado(): Boolean = false

    override fun contagiar(vectorQueContagia: Vector, vectoresAContagiar: List<Vector>) {
        //No hace nada
    }
}