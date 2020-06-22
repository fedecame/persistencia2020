package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Evento

abstract class TipoEventoModel(var evento : Evento) {
    abstract fun log() : String
}