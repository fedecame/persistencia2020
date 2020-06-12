package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.CaminoNoSoportado
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j
import net.bytebuddy.dynamic.scaffold.TypeWriter
import org.neo4j.driver.Record
import org.neo4j.driver.Value
import org.neo4j.driver.Values


class Neo4jUbicacionDAO : Neo4jDataDAO(), UbicacionDAO {
//    val session =DriverNeo4j().driver.session()

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
//        val session = TransactionNeo4j.currentSession
        val transaction = TransactionNeo4j.currentTransaction

        val query = """Match(ubicacionUno:Ubicacion {nombre:"$ubicacion1"})Match(ubicacionDos:Ubicacion{nombre:"$ubicacion2"}) MERGE (ubicacionUno)-[c:Camino {nombre:"$tipoCamino"}]->(ubicacionDos) """
        transaction.run(query)
    }

    override fun conectados(nombreDeUbicacion: String): List<Ubicacion> {
        val transaction = TransactionNeo4j.currentTransaction
        val query = """Match(:Ubicacion {nombre:${'$'}nombreDeUbicacion})-[Camino]->(ubicacionConectada:Ubicacion) Return ubicacionConectada """
        val result = transaction.run(query,Values.parameters("nombreDeUbicacion",nombreDeUbicacion))
//        var listaDeKeys = result.keys()
//        var listaDeUbicaciones = mutableListOf<Ubicacion>()
//        for (item in listaDeKeys){
//            var unaUbicacion = Ubicacion()
//            unaUbicacion.nombreUbicacion=item
//            listaDeUbicaciones.add(unaUbicacion)
//        }
//        return listaDeUbicaciones

//        val tempListUbicaciones = result.list { record: Record ->
////            val ubicacion = record.get(0)
////            val _nombre = ubicacion.get("nombre").asString()
////            mapOf(Pair("ubicacion", Ubicacion()), Pair("nombre", _nombre))
//            Ubicacion()
//        }

        val tempListNombres = result.list { record: Record ->
            val ubicacion = record.get(0)
            val _nombre = ubicacion.get("nombre").asString()
//            mapOf(Pair("ubicacion", Ubicacion()), Pair("nombre", _nombre))
            _nombre
        }

        var listaRet  = mutableListOf<Ubicacion>()
        tempListNombres.forEachIndexed { index, nombre ->
            listaRet.add(Ubicacion())
            listaRet.get(index).nombreUbicacion = nombre
        }

        return listaRet

//        result.single().get("ubicacionConectada").asString()

    }

    override fun mover(vector: Vector, nombreUbicacion:String) {
        var nombreDeUbicacionV=vector.ubicacion?.nombreUbicacion
        var ubicacionesAledañas= conectados(nombreDeUbicacionV.toString())
        var ubicacionDestino = Ubicacion()
        ubicacionDestino.nombreUbicacion=nombreUbicacion
        var ubicacionAux=ubicacionesAledañas.firstOrNull { u->u.nombreUbicacion==nombreUbicacion }
        var noPuedeMoverseTanLejos=ubicacionAux==null
        if( noPuedeMoverseTanLejos && noEsCapazDeMoverPorCamino(vector,ubicacionDestino) ){
            throw (lanzarExcepcionAlMover(noPuedeMoverseTanLejos,noEsCapazDeMoverPorCamino(vector,ubicacionDestino)))
        }
    }

    private fun lanzarExcepcionAlMover(noPuedeMoverseTanLejos: Boolean, noPuedeMoversePorCamino: Boolean): Throwable {
        var excepcion:Exception
        if(noPuedeMoverseTanLejos) {
            excepcion = UbicacionMuyLejana()
        }
        else{
            excepcion= CaminoNoSoportado()
        }
        return excepcion
    }

    private fun darTipo(camino:String) : TipoCamino? {
        when(camino){
            "Terrestre"-> return TipoCamino.Terrestre
            "Aereo"->return TipoCamino.Aereo
            "Maritimo"-> return TipoCamino.Maritimo
            else -> return null
        }
    }

    private fun noEsCapazDeMoverPorCamino(vector: Vector, ubicacionDestino: Ubicacion?):Boolean {
//        val session = TransactionNeo4j.currentSession
        val transaction = TransactionNeo4j.currentTransaction

        var  query=""" match(u1:Ubicacion{nombre:"${vector.ubicacion?.nombreUbicacion}"})-[c:Camino]-> (u2:Ubicacion{nombre:"${ubicacionDestino?.nombreUbicacion}"}) return(c.nombre)  """

        var result=   transaction.run(query)

        if(result.list().isEmpty()){
           return true
       }
               else {
           var camino = result.list().get(0)["(c.nombre)"]

           return vector.tipo.posiblesCaminos().contains(darTipo(camino.toString())).not()

       }
}

    override fun crear(ubicacion: Ubicacion): Ubicacion {
        super.crear(ubicacion.nombreUbicacion)
        return ubicacion
    }

    override fun recuperar(nombre: String): Ubicacion {
        TODO("Not yet implemented")
    }

    override fun actualizar(ubicacion: Ubicacion) {
        TODO("Not yet implemented")
    }

    override fun agregarVector(vector: Vector, ubicacion: Ubicacion) {
        TODO("Not yet implemented")
    }
}



