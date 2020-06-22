package ar.edu.unq.eperdemic.modelo.evento.tipoEvento

import ar.edu.unq.eperdemic.modelo.evento.Evento

enum class TipoEvento {
    Arribo{
        override fun aModel(evento : Evento) : TipoEventoModel = Arribo(evento)
    },
    Contagio{
        override fun aModel(evento : Evento) : TipoEventoModel = Contagio(evento)
    },
    Mutacion{
        override fun aModel(evento : Evento) : TipoEventoModel = Mutacion(evento)
    };

    abstract fun aModel(evento : Evento) : TipoEventoModel
}