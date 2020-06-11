package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class Neo4jDataService : DataDAO{

    override fun eliminarTodo() {
        TransactionRunner.addNeo4j().runTrx { Neo4jDataDAO().clear() }
    }

    override fun setDatosIniciales() {
        TransactionRunner.addNeo4j().runTrx { Neo4jDataDAO().clear() }
    }
}