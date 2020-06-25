package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.runner.FeedService

class FeedServiceImpl(private var feedDAO: FeedMongoDAO) : FeedService {

    override fun feedPatogeno(tipoDePatogeno: String): List<Evento> = feedDAO.feedPatogeno(tipoDePatogeno)

    override fun feedVector(vectorId: Long): List<Evento> {
        TODO("Not yet implemented")
    }

     override fun feedUbicacion(nombreDeUbicacion: String): List<Evento> = feedDAO.feedUbicacion(nombreDeUbicacion)

    override fun agregarEvento(evento: Evento): Evento {
        feedDAO.startTransaction()
        feedDAO.save(evento)
        feedDAO.commit()
        return evento
    }

    override fun especieYaEstabaEnLaUbicacion(nombreUbicacion: String, tipoPatogenoDeLaEspecie: String, nombreDeLaEspecie: String): Boolean = feedDAO.especieYaEstabaEnLaUbicacion(nombreUbicacion, tipoPatogenoDeLaEspecie, nombreDeLaEspecie)




}