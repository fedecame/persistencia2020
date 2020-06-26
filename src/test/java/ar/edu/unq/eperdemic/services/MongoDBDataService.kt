package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.utils.DataService

class MongoDBDataService : DataService{

    override fun crearSetDeDatosIniciales() {
        TODO("Not yet implemented")
    }

    override fun eliminarTodo() {
        FeedMongoDAO().deleteAll()
    }
}