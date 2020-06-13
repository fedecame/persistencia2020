package ar.edu.unq.eperdemic.tipo

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoCamino

class Humano() : TipoVector() {
    override var  posiblesCaminos=listOf<TipoCamino>(TipoCamino.Terrestre, TipoCamino.Maritimo)
    override fun puedeSerContagiadoPor(unTipo : TipoVector) = true

    override fun esHumano() = true

    override fun factorContagio(especie : Especie): Int =especie.factorContagioHumano()

    override fun agregarInfectado(especie: Especie) {
        especie.agregarInfectadoParaADN()
    }
}