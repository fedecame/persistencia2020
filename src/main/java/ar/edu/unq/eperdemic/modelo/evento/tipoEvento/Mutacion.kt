package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento

class Mutacion : TipoEvento() {

    override fun log(evento: Evento) : String {
        return when (evento.accionQueLoDesencadena) {
            Accion.ESPECIE_CREADA.name -> "Se crea la especie ${evento.nombreEspecie} "
            Accion.ESPECIE_MUTADA.name -> "La especie ${evento.nombreEspecie} del patogeno ${evento.tipoPatogeno} ha mutado"
            else -> ""
        }
    }
}