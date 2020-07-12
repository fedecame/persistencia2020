package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.jdbc.JDBCPatogenoDAO
import ar.edu.unq.eperdemic.utils.DataService
import ar.edu.unq.eperdemic.utils.jdbc.DataServiceJDBC

class MegalodonService : DataService {
    private val dataServices : List<DataService> =  mutableListOf(HibernateDataService(), MongoDBDataService(), Neo4jDataService(), DataServiceJDBC(JDBCPatogenoDAO()))

    override fun crearSetDeDatosIniciales() {
        dataServices.forEach { it.crearSetDeDatosIniciales() }
    }

    override fun eliminarTodo() {
        dataServices.forEach{ it.eliminarTodo() }
    }
}