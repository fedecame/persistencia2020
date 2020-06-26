package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.FeedService

class FeedServiceImpl(private var feedDAO: FeedMongoDAO) : FeedService {
 var ubicacionDao= HibernateUbicacionDAO()
    override fun feedPatogeno(tipoDePatogeno: String): List<Evento> = feedDAO.feedPatogeno(tipoDePatogeno)

    override fun feedVector(vectorId: Long): List<Evento> {
        TODO("Not yet implemented")
    }

     override fun feedUbicacion(nombreDeUbicacion: String): List<Evento> {
         var conectados= mutableListOf<String>(nombreDeUbicacion,"Florencio Varela")
         ubicacionDao.conectados(nombreDeUbicacion).forEach { u->conectados.add(u.nombreUbicacion) }

         return   feedDAO.feedUbicacion(nombreDeUbicacion, conectados)     }

    override fun agregarEvento(evento: Evento): Evento {
        feedDAO.startTransaction()
        feedDAO.save(evento)
        feedDAO.commit()
        return evento
    }

    override fun especieYaEstabaEnLaUbicacion(nombreUbicacion: String, tipoPatogenoDeLaEspecie: String, nombreDeLaEspecie: String): Boolean = feedDAO.especieYaEstabaEnLaUbicacion(nombreUbicacion, tipoPatogenoDeLaEspecie, nombreDeLaEspecie)
     fun vectorFueContagiadoAlMover(nombreUbicacion: String, idVectorInfectado:Int, idVectorAInfectar: Int): Boolean = feedDAO.vectorFueContagiadoAlMover(nombreUbicacion, idVectorInfectado, idVectorAInfectar)





}