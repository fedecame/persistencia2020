package ar.edu.unq.eperdemic.modelo

class TipoHumano(vector: Vector) : TipoVector(vector) {
    override fun puedeSerContagiadoPor(unTipo : TipoVector) = true

    override fun esHumano() = true

    override fun factorContagio(especie : Especie): Int = especie.factorContagioHumano()
}