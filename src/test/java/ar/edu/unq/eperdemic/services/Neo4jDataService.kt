package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utils.DataService

class Neo4jDataService : DataService{
    //NonePlace es un nodo huerfano
    private val ubicaciones = listOf("Quilmes", "Remedios de Escalada", "Ezpeleta", "Narnia", "Babilonia", "Zion", "Mordor", "Wonderland", "NonePlace")

    //Esto genera un grafo como el de la imagen
    override fun crearSetDeDatosIniciales() {
        ubicaciones.forEach { TransactionRunner.addNeo4j().runTrx { Neo4jDataDAO().crear(it)} }
        this.conectarUnidireccionales()
        this.conectarBidireccionales()
    }

    private fun conectarUnidireccionales(){
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectUni("Quiles", "Babilonia", "Aereo")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectUni("Quiles", "Remedios de Escalada", "Aereo")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectUni("Zion", "WonderLand", "Aereo")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectUni("Zion", "Babilonia", "Terrestre")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectUni("Ezpeleta", "Mordor", "Maritimo")}
    }

    private fun conectarBidireccionales() {
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectBi("Zion", "Mordor", "Aereo")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectBi("Mordor", "Narnia", "Terrestre")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectBi("Quiles", "Ezpeleta", "Terrestre")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectBi("Babilonia", "Ezpeleta", "Maritimo")}
        TransactionRunner.addNeo4j().runTrx {Neo4jDataDAO().conectBi("Narnia", "Ezpeleta", "Maritimo")}
    }

    override fun eliminarTodo() {
        TransactionRunner.addNeo4j().runTrx { Neo4jDataDAO().clear() }
    }
}