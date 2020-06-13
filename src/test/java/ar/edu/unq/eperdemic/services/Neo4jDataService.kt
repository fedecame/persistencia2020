package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utils.DataService

class Neo4jDataService : DataService{
    //NonePlace es un nodo huerfano
    private val ubicaciones = listOf("Quilmes", "Remedios de Escalada", "Ezpeleta", "Narnia", "Babilonia", "Zion", "Mordor", "WonderLand", "elNodoSolitario")
    val neo4jDataDAO = Neo4jDataDAO()
    val ubicacionService = UbicacionServiceImpl(HibernateUbicacionDAO())

    //Esto genera un grafo como el de la imagen
    override fun crearSetDeDatosIniciales() {
        ubicaciones.forEach {ubicacionService.crearUbicacion(it)}
        TransactionRunner.addNeo4j().runTrx {
            this.conectarUnidireccionales()
            this.conectarBidireccionales()
        }
    }

    private fun conectarUnidireccionales() {
        neo4jDataDAO.conectUni("Quilmes", "Babilonia", "Aereo")
        neo4jDataDAO.conectUni("Quilmes", "Remedios de Escalada", "Aereo")
        neo4jDataDAO.conectUni("Zion", "WonderLand", "Aereo")
        neo4jDataDAO.conectUni("Zion", "Babilonia", "Terrestre")
        neo4jDataDAO.conectUni("Ezpeleta", "Mordor", "Maritimo")
    }

    private fun conectarBidireccionales() {
        neo4jDataDAO.conectBi("Zion", "Mordor", "Aereo")
        neo4jDataDAO.conectBi("Mordor", "Narnia", "Terrestre")
        neo4jDataDAO.conectBi("Quiles", "Ezpeleta", "Terrestre")
        neo4jDataDAO.conectBi("Babilonia", "Ezpeleta", "Maritimo")
        neo4jDataDAO.conectBi("Narnia", "Ezpeleta", "Maritimo")
    }

    override fun eliminarTodo() {
        TransactionRunner.addNeo4j().runTrx { neo4jDataDAO.clear() }
    }
}