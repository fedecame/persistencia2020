package ar.edu.unq.eperdemic.modelo

class Infectado(val vector: Vector) : EstadoVector {
    override fun contagiar(vectoresAContagiar: List<Vector>) {
        vectoresAContagiar.forEach{vectorContagiable -> vectorContagiable.contagiarsePor(vector)}
    }
}