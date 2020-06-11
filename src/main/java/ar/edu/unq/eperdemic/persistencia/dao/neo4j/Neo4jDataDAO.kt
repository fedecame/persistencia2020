package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j

class Neo4jDataDAO : DataDAO {
    override fun clear() {
        val transaction = TransactionNeo4j.currentTransaction
        val deleteAllQuery = """
            MATCH (n)
            DETACH DELETE n
        """.trimIndent()
        transaction.run(deleteAllQuery)
    }
}