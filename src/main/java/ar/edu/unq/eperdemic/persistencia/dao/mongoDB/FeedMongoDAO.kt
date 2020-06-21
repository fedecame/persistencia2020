package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.modelo.Evento
import ar.edu.unq.eperdemic.persistencia.dao.FeedDAO

class FeedMongoDAO : GenericMongoDAO<Evento>(Evento::class.java), FeedDAO {

    override fun eventosDeContagioPorPandemia(s: String): List<Evento> {
        return listOf()
    }

}