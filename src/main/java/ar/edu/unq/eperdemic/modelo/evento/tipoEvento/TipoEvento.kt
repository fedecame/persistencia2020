package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Evento

enum class TipoEvento {
    Arribo{
        override fun aModel(evento : Evento) : TipoEventoModel = Arribo()
    },
    Contagio{
        override fun aModel(evento : Evento) : TipoEventoModel = Contagio()
    },
    Mutacion{
        override fun aModel(evento : Evento) : TipoEventoModel = Mutacion()
    };

    abstract fun aModel(evento : Evento) : TipoEventoModel
}