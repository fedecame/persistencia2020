package ar.edu.unq.eperdemic.modelo

interface EstadoVector {
    fun contagiar(vectoresAContagiar: List<Vector>)
    fun infectarse (vector:Vector)
}