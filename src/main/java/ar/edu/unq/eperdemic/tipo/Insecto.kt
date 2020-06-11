package ar.edu.unq.eperdemic.tipo

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoCamino

class Insecto() : TipoVector() {
    override var posiblesCaminos=listOf<TipoCamino>(TipoCamino.Aereo)

    override fun puedeSerContagiadoPor(tipo : TipoVector): Boolean =  tipo.esHumano() || tipo.esAnimal()

    override fun esInsecto() = true

    override fun factorContagio(especie : Especie): Int = especie.factorContagioInsecto()
}