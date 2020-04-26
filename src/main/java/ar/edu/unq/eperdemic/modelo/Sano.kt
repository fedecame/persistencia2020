package ar.edu.unq.eperdemic.modelo

class Sano() : EstadoVector{
    override fun infectarse(vector: Vector) {
       vector.cambiarEstado(this)
    }

    override fun contagiar(vectoresAContagiar: List<Vector>) {
        //No hace nada
    }

}