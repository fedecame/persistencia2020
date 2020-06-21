package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Evento
import ar.edu.unq.eperdemic.services.runner.FeedService

class FeedServiceImpl : FeedService {
    override fun feedPatogeno(tipoDePatogeno: String): List<Evento> {
        TODO("Not yet implemented")
    }

    override fun feedVector(vectorId: Long): List<Evento> {
        TODO("Not yet implemented")
    }

    override fun feedUbicacion(nombreDeUbicacion: String): List<Evento> {
        TODO("Not yet implemented")
    }
}