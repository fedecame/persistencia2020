package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie

interface EstadisticasDAO {

    fun vectoresPresentes(nombreUbicacion : String) : Int

    fun vectoresInfectados(nombreUbicacion : String) : Int

    fun especieQueInfectaAMasVectoresEn(nombreUbicacion : String) : String
    fun especieLider(): Especie

    fun lideres(): MutableList<Especie>
}