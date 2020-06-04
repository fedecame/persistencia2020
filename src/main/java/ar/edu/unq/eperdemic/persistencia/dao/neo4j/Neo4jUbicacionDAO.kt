package ar.edu.unq.eperdemic.persistencia.dao.neo4j

class Neo4jUbicacionDAO {
    var driverNeo4j = DriverNeo4j()
    fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        driverNeo4j.driver.session().use { session ->
            session.writeTransaction {
                val query = "Create ($ubicacion1-(:$tipoCamino)->$ubicacion2"
            }
        }
    }
}



