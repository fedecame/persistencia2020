package ar.edu.unq.eperdemic.modelo.evento

import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Arribo
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio

class EventoFactory {
    private var n = 0
    //Aca faltan los parametros para que arme los eventos con la data que necesitamos
 fun eventoPorArribo(ubicacionInicial: String, nombreUbicacion: String,idVector:Int): Evento =Evento(n++,Arribo(),Accion.Arribo_A_Ubicacion.name,null,null,nombreUbicacion,ubicacionInicial,idVector)
   fun eventoPorContagio(nombreUbicacion: String, vectorIdInfectado: Int,vectorId: Int)=Evento(n++,Contagio(),Accion.PADECE_ENFERMEDAD.name,null,null,nombreUbicacion,null,vectorIdInfectado,vectorId)
    fun eventoContagioPorPandemia(tipoPatogeno: String, especieNombre: String): Evento = Evento(++n, Contagio(), Accion.PATOGENO_ES_PANDEMIA.name, tipoPatogeno, especieNombre, null)
    fun eventoContagioPorPrimeraVezEnUbicacion(tipoPatogeno: String, nombreUbicacion: String, nombreEspecie: String) : Evento = Evento(++n, Contagio(),Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name, tipoPatogeno, nombreEspecie, nombreUbicacion)
    fun eventoPorArriboYContagio( nombreUbicacion: String, vectorId: Int): Evento =Evento(++n, Arribo(),Accion.Vector_Contagia_Al_Mover.name, null, null, nombreUbicacion,null,vectorId)


}