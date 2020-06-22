package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Evento

class Mutacion  (evento: Evento) : TipoEventoModel(evento){
    override fun log(): String {
        TODO("Not yet implemented")
    }
}