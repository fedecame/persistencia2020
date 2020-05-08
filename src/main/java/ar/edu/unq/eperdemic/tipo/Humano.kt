package ar.edu.unq.eperdemic.tipo

import ar.edu.unq.eperdemic.modelo.Especie

class Humano() : TipoVector() {
    override fun puedeSerContagiadoPor(unTipo : TipoVector) = true

    override fun esHumano() = true

    override fun factorContagio(especie : Especie): Int =especie.factorContagioHumano()

    override fun agregarInfectado(especie: Especie) {
        // esto suma 1 a la cant d infectados q valen para el adn en la especie del parametro
        //especie.agregarInfectado()
    }
}