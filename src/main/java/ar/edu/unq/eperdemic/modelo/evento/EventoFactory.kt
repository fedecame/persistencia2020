package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio

class EventoFactory {
    private var n = 0
    //Aca faltan los parametros para que arme los eventos con la data que necesitamos
    fun eventoContagioPorPandemia(tipoPatogeno: String, especieNombre: String): Evento = Evento(++n, Contagio(), Accion.PATOGENO_ES_PANDEMIA.name, tipoPatogeno, especieNombre)
    fun eventoContagioPorPrimeraVezEnUbicacion(tipoPatogeno : String) : Evento = Evento(++n, Contagio(),Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name, tipoPatogeno)
}