package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEvento

class EventoFactory {
    //Aca faltan los parametros para que arme los eventos con la data que necesitamos
    fun eventoContagioPorPandemia(tipoPatogeno : String): Evento = Evento(TipoEvento.Contagio, Accion.PATOGENO_ES_PANDEMIA.toString(), tipoPatogeno)
    fun eventoContagioPorPrimeraVezEnUbicacion(tipoPatogeno : String) : Evento = Evento(TipoEvento.Contagio,Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.toString(), tipoPatogeno)
}