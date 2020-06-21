package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.evento.Evento

interface FeedDAO {

    fun eventosDeContagioPorPandemia(tipoPatogeno : String) : List<Evento>
}