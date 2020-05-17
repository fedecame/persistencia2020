package ar.edu.unq.eperdemic.persistencia.dao

interface EstadisticasDAO {

    fun vectoresPresentes(nombreUbicacion : String) : Int

    fun vectoresInfectados(nombreUbicacion : String) : Int

    fun especieQueInfectaAMasVectoresEn(nombreUbicacion : String) : String
}