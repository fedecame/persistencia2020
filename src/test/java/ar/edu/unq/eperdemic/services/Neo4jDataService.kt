package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class Neo4jDataService {
    fun eliminarTodo() {
        TransactionRunner.addNeo4j().runTrx { Neo4jDataDAO().clear() }
    }
}