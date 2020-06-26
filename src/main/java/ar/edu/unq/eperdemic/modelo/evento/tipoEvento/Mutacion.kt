package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento

class Mutacion : TipoEvento() {

    override fun log(evento: Evento) : String {
        return when (evento.accionQueLoDesencadena) {
            Accion.MUTACION_CREADA.name -> "Se crea mutacion"
            Accion.MUTACION_CREADA.name -> "La especie ${evento.nombreEspecie} del patogeno ${evento.tipoPatogeno} ha mutado"
            else -> ""
        }
    }
}