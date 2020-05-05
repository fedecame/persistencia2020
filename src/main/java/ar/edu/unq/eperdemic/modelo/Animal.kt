package ar.edu.unq.eperdemic.modelo

class Animal() : TipoVector() {
    override fun puedeSerContagiadoPor(tipo : TipoVector) = true //tipo.esInsecto()
    override fun esAnimal() = true

    override fun factorContagio(especie : Especie): Int = 2//especie.factorContagioAnimal()
}