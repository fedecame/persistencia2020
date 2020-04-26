package ar.edu.unq.eperdemic.modelo

class TipoAnimal(vector: Vector) : TipoVector(vector) {
    override fun puedeSerContagiadoPor(tipo : TipoVector) = tipo.esInsecto()
    override fun esAnimal() = true

    override fun factorContagio(especie : Especie): Int = especie.factorContagioAnimal()
}