package ar.edu.unq.eperdemic.modelo

interface EstadoVector {
    fun contagiar(vectorContangia : Vector, vectoresAContagiar: List<Vector>)
    fun infectarse (vector:Vector)
    fun nombre() : String
}