package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.modelo.Evento

class FeedMongoDAO : GenericMongoDAO<Evento>(Evento::class.java) {


}