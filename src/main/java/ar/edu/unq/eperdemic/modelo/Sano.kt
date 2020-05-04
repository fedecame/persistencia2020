package ar.edu.unq.eperdemic.modelo

class Sano() : EstadoVector{
    override fun infectarse(vector: Vector) {
       //vector.cambiarEstado(this)
    }

    override fun nombre(): String =  EstadoDeVector.Sano.name.toString()

    override fun contagiar(vectorQueContagia: Vector, vectoresAContagiar: List<Vector>) {
        //No hace nada
    }

}