package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.utils.DataService

class MegalodonService : DataService {
    private val dataServices : List<DataService> =  mutableListOf(HibernateDataService(), MongoDBDataService() , Neo4jDataService())

    override fun crearSetDeDatosIniciales() {
        dataServices.forEach { it.crearSetDeDatosIniciales() }
    }

    override fun eliminarTodo() {
        dataServices.forEach{ it.eliminarTodo() }
    }
}