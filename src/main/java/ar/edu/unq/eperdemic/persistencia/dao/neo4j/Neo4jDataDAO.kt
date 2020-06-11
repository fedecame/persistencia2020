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

    fun crear(ubicacionNombre: String) {
        val transaction = TransactionNeo4j.currentTransaction
        val addQuery = """CREATE (ALIAS:Ubicacion { nombre: "$ubicacionNombre" }) return ALIAS""".trimIndent()
        transaction.run(addQuery)
    }

    fun conectUni(ubicacionOrigin: String, ubicacionFinal: String, tipo : String) {
        val transaction = TransactionNeo4j.currentTransaction
        val conectQuery = """MATCH (a:Ubicacion),(b:Ubicacion)
        WHERE a.nombre = "$ubicacionOrigin" AND b.nombre = "$ubicacionFinal"
        CREATE (a)-[r:"$tipo"]->(b)"""
        transaction.run(conectQuery)
    }

    fun conectBi(ubicacionOrigin: String, ubicacionFinal: String, tipo : String) {
        this.conectUni(ubicacionOrigin, ubicacionFinal, tipo)
        this.conectUni(ubicacionFinal, ubicacionOrigin, tipo)
    }
}