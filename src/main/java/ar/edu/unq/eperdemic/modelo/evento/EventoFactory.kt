package ar.edu.unq.eperdemic.modelo.evento

class EventoFactory {

    //Aca faltan los parametros para que arme los eventos con la data que necesitamos
    fun eventoContagioPorPandemia() : Evento = Contagio()
    fun eventoContagioPOrPandemia() : Evento = Contagio()
}