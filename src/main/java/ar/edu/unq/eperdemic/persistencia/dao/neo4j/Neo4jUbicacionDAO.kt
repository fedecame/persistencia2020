package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.CaminoNoSoportado
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.modelo.exception.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.modelo.exception.UbicacionNoEncontradaRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.neo4j.driver.Values
import net.bytebuddy.dynamic.scaffold.TypeWriter
import org.neo4j.driver.Record
import org.neo4j.driver.Value


class Neo4jUbicacionDAO : Neo4jDataDAO(), UbicacionDAO {
    val vectorDao = HibernateVectorDAO()
    var hibernateUbicacionDAO = HibernateUbicacionDAO()
    var myFormatter : MyFormat = MyFormatter()

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        val transaction = TransactionNeo4j.currentTransaction
        val query = """Match(ubicacionUno:Ubicacion {nombre:"$ubicacion1"}),(ubicacionDos:Ubicacion{nombre:"$ubicacion2"}) MERGE (ubicacionUno)-[c:$tipoCamino]->(ubicacionDos)"""
        transaction.run(query)
    }

    override fun conectados(nombreDeUbicacion: String): List<Ubicacion> {
        val transaction = TransactionNeo4j.currentTransaction
        val query = """Match(:Ubicacion {nombre:${'$'}nombreDeUbicacion})-[c]->(ubicacionConectada:Ubicacion) Return ubicacionConectada """
        val result = transaction.run(query,Values.parameters("nombreDeUbicacion",nombreDeUbicacion))

        val tempListNombres = result.list { record: Record ->
            val ubicacion = record.get(0)
            val _nombre = ubicacion.get("nombre").asString()
            val ubicacionTemp = Ubicacion()
            ubicacionTemp.nombreUbicacion = _nombre
            ubicacionTemp
        }
        return tempListNombres
    }

    override fun mover(vector: Vector, nombreUbicacion: String) {
//        TODO("Should not be implemented")
    }

    fun esAledaña(nombreDeUbicacion: String, uPosibleAledaña: String) {
        var transaction = TransactionNeo4j.currentTransaction
        var query = """Match(u1:Ubicacion {nombre:"$nombreDeUbicacion"})-[c]->(u2:Ubicacion {nombre:"$uPosibleAledaña"}) return(c)"""
        var result = transaction.run(query)
        if (result.list().isEmpty()) {
            throw UbicacionMuyLejana(nombreDeUbicacion,uPosibleAledaña)
        }
    }

    fun noEsCapazDeMoverPorCamino(vector: Vector, ubicacionDestino: String) {
        var transaction = TransactionNeo4j.currentTransaction
        var nombreUbicacionActual = vector.ubicacion!!.nombreUbicacion
        val tiposDeCaminosPosibles = vector.tipo.posiblesCaminos.map { it.name }
        val tiposDeLaRelacion = tiposDeCaminosPosibles.toString().replace(",", "|").trim().drop(1).dropLast(1).toString()

        var query = """ match(u1:Ubicacion{nombre:"$nombreUbicacionActual"})-[c:${tiposDeLaRelacion}]-> (u2:Ubicacion{nombre:"$ubicacionDestino"}) return(c)  """
        var result = transaction.run(query).list()
        if (result.isEmpty()){
            throw CaminoNoSoportado(nombreUbicacionActual, ubicacionDestino)
        }
    }

    override fun crear(ubicacion: Ubicacion): Ubicacion {
        super.crear(ubicacion.nombreUbicacion)
        return ubicacion
    }

    override fun recuperar(nombre: String): Ubicacion {
        val transaction = TransactionNeo4j.currentTransaction
        val query = """ MATCH (n:Ubicacion) WHERE n.nombre = "${nombre}"
                       RETURN n """
        val result = transaction.run(query, Values.parameters("nombre", nombre)).single()["n"]["nombre"]
        val ubicacion = Ubicacion()
        ubicacion.nombreUbicacion = result.toString().replace("\"", "")
        if(result == null){
            throw UbicacionNoEncontradaRunTimeException(nombre)
        }
        return ubicacion
    }

    override fun actualizar(ubicacion: Ubicacion) {
//        TODO("Not yet implemented")
    }

    override fun agregarVector(vector: Vector, ubicacion: Ubicacion) {
//        TODO("Not yet implemented")
    }

    override fun capacidadDeExpansion(vectorId: Long, movimientos: Int): Int {
        val vector = vectorDao.recuperar(vectorId)
        val nombreUbicacion = vector.ubicacion!!.nombreUbicacion
        val tipos = vector.tipo.posiblesCaminos.map{it.nombre()}

        val tiposQueryConMovimientos = myFormatter.tiposFormateados(tipos, movimientos)
        val transaction = TransactionNeo4j.currentTransaction
        val intQuery =  """
                        MATCH (n:Ubicacion {nombre:"${nombreUbicacion}"})-${tiposQueryConMovimientos} -> (fof) WHERE fof.nombre <> n.nombre RETURN COUNT(DISTINCT fof) AS result
                        """
        val result = transaction.run(intQuery, Values.parameters("nombreUbicacion", nombreUbicacion, "tiposQueryConMovimientos", tiposQueryConMovimientos, "movimientos", movimientos))
        return result.single().get("result").asInt()
    }

    override fun moverMasCorto(vector: Vector, ubicacion: Ubicacion) {
        val transaction = TransactionNeo4j.currentTransaction
        val tiposDeCaminosPosibles = vector.tipo.posiblesCaminos.map { it.name }
        val tiposDeLaRelacion = myFormatter.caminosFormateados(tiposDeCaminosPosibles)

        val query = """
            MATCH p=shortestPath(
            (salida:Ubicacion {nombre:${'$'}nombreSalida})-[:${tiposDeLaRelacion}*]->(llegada:Ubicacion {nombre:${'$'}nombreLlegada})
            )
            RETURN p
        """.trimIndent()

        val caminoMasCorto = transaction.run(query, Values.parameters(
                "nombreSalida", vector.ubicacion!!.nombreUbicacion,
                "tiposDeLaRelacion", tiposDeLaRelacion,
                "nombreLlegada", ubicacion.nombreUbicacion
        ))

        lateinit var nombreUbicaciones: List<String>
        try {
            nombreUbicaciones = caminoMasCorto.single().get("p").asPath().nodes().map { it.get("nombre").toString().drop(1).dropLast(1) }
        } catch (err: Throwable) {
            throw UbicacionNoAlcanzable()
        }

        this.moverPorUbicaciones(vector, nombreUbicaciones.drop(1))
    }

    private fun moverPorUbicaciones(vector: Vector, nombresDeUbicaciones: List<String>) {
        nombresDeUbicaciones.forEach { hibernateUbicacionDAO.mover(vector, it) }
    }
}