package ar.edu.unq.eperdemic.estado

import ar.edu.unq.eperdemic.modelo.Vector

class Sano() : EstadoVector {
    override fun infectarse(vector: Vector) {
       //vector.cambiarEstado(this)
    }

    override fun nombre(): String =  "Sano"

    override fun contagiar(vectorQueContagia: Vector, vectoresAContagiar: List<Vector>) {
        //No hace nada
    }

}