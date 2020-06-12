package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utils.DataService

class Neo4jDataService : DataService{
    //NonePlace es un nodo huerfano
    private val ubicaciones = listOf("Quilmes", "Remedios de Escalada", "Ezpeleta", "Narnia", "Babilonia", "Zion", "Mordor", "Wonderland", "NonePlace","Mar Del Plata")
    val neo4jDataDAO = Neo4jDataDAO()

    //Esto genera un grafo como el de la imagen
    override fun crearSetDeDatosIniciales() {
        TransactionRunner.addNeo4j().runTrx {
            ubicaciones.forEach {neo4jDataDAO.crear(it)}
            this.conectarUnidireccionales()
            this.conectarBidireccionales()
        }
    }





    private fun conectarUnidireccionales() {
        neo4jDataDAO.conectUni("Mar Del Plata", "Quilmes", "Terrestre")

        neo4jDataDAO.conectUni("Quilmes", "Babilonia", "Aereo")
        neo4jDataDAO.conectUni("Quilmes", "Remedios de Escalada", "Aereo")
        neo4jDataDAO.conectUni("Zion", "WonderLand", "Aereo")
        neo4jDataDAO.conectUni("Zion", "Babilonia", "Terrestre")
        neo4jDataDAO.conectUni("Ezpeleta", "Mordor", "Maritimo")
    }

    private fun conectarBidireccionales() {
        neo4jDataDAO.conectBi("Zion", "Mordor", "Aereo")
        neo4jDataDAO.conectBi("Mordor", "Narnia", "Terrestre")
        neo4jDataDAO.conectBi("Quilmes", "Ezpeleta", "Terrestre")
        neo4jDataDAO.conectBi("Babilonia", "Ezpeleta", "Maritimo")
        neo4jDataDAO.conectBi("Narnia", "Ezpeleta", "Maritimo")
    }


    override fun eliminarTodo() {
        TransactionRunner.addNeo4j().runTrx { neo4jDataDAO.clear() }
    }

    fun datosParaEstadisticaService(){
        TransactionRunner.addNeo4j().runTrx {
            neo4jDataDAO.crear2("Quilmes")
            neo4jDataDAO.crear2("Mar Del Plata")
            neo4jDataDAO.crear2("Mar del Plata")
            neo4jDataDAO.crear2("Florencio Varela")
            neo4jDataDAO.crear2("Maeame")

        }
        }
    fun datosParaUbicacionService(){
        TransactionRunner.addNeo4j().runTrx {
            neo4jDataDAO.crear2("Quilmes")
            neo4jDataDAO.crear2("Florencio Varela")
            neo4jDataDAO.crear2("Berazategui")
            neo4jDataDAO.crear2("Sarandi")

        }
    }
    fun datosParaVectorService(){
        TransactionRunner.addNeo4j().runTrx {
            neo4jDataDAO.crear2("Alemania")
            neo4jDataDAO.crear2("Florencio Varela")
            neo4jDataDAO.crear2("Berazategui")
            neo4jDataDAO.crear2("Quilmes")

        }
    }
}