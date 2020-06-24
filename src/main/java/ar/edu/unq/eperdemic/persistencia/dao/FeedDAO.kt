package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.evento.Evento

interface FeedDAO {
    fun feedPatogeno(tipoPatogeno : String) : List<Evento>
    fun feedVector(tipoPatogeno : String) : List<Evento>
    fun feedUbicacion(tipoPatogeno : String) : List<Evento>
    fun especieYaEstabaEnLaUbicacion(nombreUbicacion: String, tipoPatogenoDeLaEspecie: String, nombreEspecie : String): Boolean
}