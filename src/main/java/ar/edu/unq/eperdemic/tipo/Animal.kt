package ar.edu.unq.eperdemic.tipo

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoCamino

class Animal() : TipoVector() {
    override var posiblesCaminos=listOf<TipoCamino>(TipoCamino.Terrestre, TipoCamino.Maritimo, TipoCamino.Aereo)
    override fun puedeSerContagiadoPor(tipo : TipoVector) = tipo.esInsecto()
    override fun esAnimal() = true
    override fun factorContagio(especie : Especie): Int = especie.factorContagioAnimal()
}