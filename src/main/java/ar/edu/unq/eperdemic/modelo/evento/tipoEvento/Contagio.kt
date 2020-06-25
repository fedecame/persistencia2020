package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Evento

class Contagio : TipoEvento(){
    override fun log(evento: Evento): String = ""
}