package ar.edu.unq.eperdemic.modelo

class TipoInsecto(vector: Vector) : TipoVector(vector) {
    override fun puedeSerContagiadoPor(tipo : TipoVector): Boolean = tipo.esHumano() || tipo.esAnimal()

    override fun esInsecto() = true

    override fun factorContagio(especie : Especie): Int = especie.factorContagioInsecto()
}