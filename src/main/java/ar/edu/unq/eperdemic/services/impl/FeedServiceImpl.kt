package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.runner.FeedService

class FeedServiceImpl(private var feedDAO: FeedMongoDAO) : FeedService {

    override fun feedPatogeno(tipoDePatogeno: String): List<Evento> = feedDAO.feedPatogeno(tipoDePatogeno)

    override fun feedVector(vectorId: Long): List<Evento> {
        TODO("Not yet implemented")
    }

    override fun feedUbicacion(nombreDeUbicacion: String): List<Evento> {
        TODO("Not yet implemented")
    }
}