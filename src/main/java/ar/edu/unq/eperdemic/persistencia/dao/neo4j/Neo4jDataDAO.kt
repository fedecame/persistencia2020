package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j
import org.neo4j.driver.Values

open class Neo4jDataDAO : DataDAO {
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
        val addQuery = """CREATE (ubicacion:Ubicacion { nombre: ${'$'}ubicacionNombre }) return ubicacion""".trimIndent()
        transaction.run(addQuery, Values.parameters("ubicacionNombre", ubicacionNombre))
    }
    fun crear2(ubicacionNombre:String){
        val transaction = TransactionNeo4j.currentTransaction
        val addQuery =""" Create(u:Ubicacion {nombre:"$ubicacionNombre"})return(u) """
        transaction.run(addQuery)
    }

    fun conectUni(ubicacionOrigin: String, ubicacionFinal: String, tipo : String) {
        val transaction = TransactionNeo4j.currentTransaction
        val conectQuery = """MATCH (a:Ubicacion),(b:Ubicacion)
        WHERE a.nombre = ${'$'}ubicacionOrigin AND b.nombre = ${'$'}ubicacionFinal
        CREATE (a)-[:${tipo}]->(b)""".trimIndent()
        transaction.run(conectQuery, Values.parameters(
            "ubicacionOrigin", ubicacionOrigin,
            "ubicacionFinal", ubicacionFinal
        ))
    }

    fun conectBi(ubicacionOrigin: String, ubicacionFinal: String, tipo : String) {
        this.conectUni(ubicacionOrigin, ubicacionFinal, tipo)
        this.conectUni(ubicacionFinal, ubicacionOrigin, tipo)
    }
}