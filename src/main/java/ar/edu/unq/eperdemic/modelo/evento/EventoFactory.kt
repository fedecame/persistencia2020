package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Mutacion

object EventoFactory {
    //Aca faltan los parametros para que arme los eventos con la data que necesitamos
    fun eventoEspecieCreada(tipoPatogeno: String, especieNombre: String): Evento = Evento(Mutacion(), Accion.ESPECIE_CREADA.name, tipoPatogeno, especieNombre)
    fun eventoEspecieDePatogenoMuta(tipoPatogeno: String, especieNombre: String): Evento= Evento(Mutacion(), Accion.ESPECIE_MUTADA.name , tipoPatogeno, especieNombre)
    fun eventoContagioPorPandemia(tipoPatogeno: String, especieNombre: String): Evento = Evento(Contagio(), Accion.PATOGENO_ES_PANDEMIA.name, tipoPatogeno, especieNombre, null)
    fun eventoContagioPorPrimeraVezEnUbicacion(tipoPatogeno: String, nombreUbicacion: String, nombreEspecie: String) : Evento = Evento(Contagio(),Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name, tipoPatogeno, nombreEspecie, nombreUbicacion)
}