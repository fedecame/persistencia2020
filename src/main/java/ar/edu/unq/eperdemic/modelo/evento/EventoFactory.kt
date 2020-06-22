package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Arribo
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio

class EventoFactory {
    //Aca faltan los parametros para que arme los eventos con la data que necesitamos
    fun eventoContagioPorPandemia(tipoPatogeno : String): Evento = Evento(Contagio(), Accion.PATOGENO_ES_PANDEMIA.name, tipoPatogeno)
    fun eventoContagioPorPrimeraVezEnUbicacion(tipoPatogeno : String) : Evento = Evento(Contagio(),Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.toString(), tipoPatogeno)
    fun eventoPorArribo(ubicacionInicial: String, nombreUbicacion: String): Evento =Evento(Arribo(),Accion.Arribo_A_Ubicacion.toString(),ubicacionInicial)


}