package ar.edu.unq.eperdemic.modelo

class Infectado() : EstadoVector {
    override fun infectarse(vector: Vector) {
        //No hace nada
    }

    override fun nombre(): String = EstadoDeVector.Infectado.name.toString()

    override fun contagiar(vectorContangia : Vector, vectoresAContagiar: List<Vector>) {
        //vectoresAContagiar.forEach{vectorContagiable -> vectorContagiable.contagiarsePor(vector)}
    }
}