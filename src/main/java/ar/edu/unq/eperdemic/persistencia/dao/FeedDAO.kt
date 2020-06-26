package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.evento.Evento

interface FeedDAO {
    fun feedPatogeno(tipoPatogeno : String) : List<Evento>
    fun feedVector(vectorId : Long) : List<Evento>
    fun feedUbicacion(nombreUbicacion : String, conectados : List<String>) : List<Evento>
    fun especieYaEstabaEnLaUbicacion(nombreUbicacion: String, tipoPatogenoDeLaEspecie: String, nombreEspecie : String): Boolean
    fun especieYaEsPandemia(tipoPatogenoDeLaEspecie: String, nombreEspecie : String): Boolean
}