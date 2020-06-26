package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio

object EventoFactory {
    //Aca faltan los parametros para que arme los eventos con la data que necesitamos
    fun eventoContagioPorPandemia(tipoPatogeno: String, especieNombre: String): Evento = Evento(Contagio(), Accion.PATOGENO_ES_PANDEMIA.name, tipoPatogeno, especieNombre, null)
    fun eventoContagioPorPrimeraVezEnUbicacion(tipoPatogeno: String, nombreUbicacion: String, nombreEspecie: String) : Evento = Evento(Contagio(),Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name, tipoPatogeno, nombreEspecie, nombreUbicacion)
    fun eventoContagioNormal(vectorQueInfectaId: Long?, vectorInfectadoId: Long, ubicacionDeContagio: String?): Evento = Evento(Contagio(), Accion.CONTAGIO_NORMAL.name, _nombreUbicacion = ubicacionDeContagio, _idVectorInfectado = vectorInfectadoId, _idVectorQueInfecta = vectorQueInfectaId)
}