package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.persistencia.dao.FeedDAO

class FeedMongoDAO : GenericMongoDAO<Evento>(Evento::class.java), FeedDAO {

    override fun feedPatogeno(tipoPatogeno : String) : List<Evento>{
        return listOf()
    }

    override fun feedVector(tipoPatogeno: String): List<Evento> {
        TODO("Not yet implemented")
    }

    override fun feedUbicacion(tipoPatogeno: String): List<Evento> {
        TODO("Not yet implemented")
    }
}