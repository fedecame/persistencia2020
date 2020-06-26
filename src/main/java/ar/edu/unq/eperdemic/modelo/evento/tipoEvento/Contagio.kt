package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento

class Contagio : TipoEvento() {
    override fun log(evento: Evento) : String{
        val inicio0 = "El patogeno ${evento.tipoPatogeno}"
        val inicio1 = inicio0 + " contagio al vector ${evento.idVectorinfectado} con la especie ${evento.nombreEspecie} en la ubicacion ${evento.ubicacionContagio} "
        return when (evento.accionQueLoDesencadena) {
            Accion.PATOGENO_ES_PANDEMIA.name -> inicio0 + " se volvio pandemia"
            Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name -> inicio1 + " por primera vez"
            Accion.CONTAGIO_NORMAL.name -> "El vector ${evento.idVectorQueInfecta} infecto al vector ${evento.idVectorinfectado} en la ubicacion ${evento.ubicacionContagio}"
            else -> ""
        }
    }
}