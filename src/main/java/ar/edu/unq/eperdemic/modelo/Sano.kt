package ar.edu.unq.eperdemic.modelo

class Sano() : EstadoVector{
    override fun infectarse(vector: Vector) {
       //vector.cambiarEstado(this)
    }

    override fun nombre(): String = EstadoDelivery.EstadoVectorEnum.Sano.name

    override fun contagiar(vectorQueContagia: Vector, vectoresAContagiar: List<Vector>) {
        //No hace nada
    }

}