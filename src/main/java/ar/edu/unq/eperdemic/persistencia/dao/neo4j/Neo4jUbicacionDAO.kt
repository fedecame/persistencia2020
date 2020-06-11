package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.CaminoNoSoportado
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.services.runner.DriverNeo4j
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j


class Neo4jUbicacionDAO :UbicacionDaoNeo4j{
    val session =DriverNeo4j().driver.session()

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {


        val query = """Match(ubicacionUno:Ubicacion {nombre:"$ubicacion1"})Match(ubicacionDos:Ubicacion{nombre:"$ubicacion2"}) MERGE (ubicacionUno)-[c:Camino {nombre:"$tipoCamino"}]->(ubicacionDos) """
        session.run(query)

    }


    override fun conectados(nombreDeUbicacion:String): List<Ubicacion>{
       val ubicacion= Ubicacion()
        ubicacion.nombreUbicacion="TibetDojo"

        val list= listOf(ubicacion)
        return list
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
        var  query=""" match(u1:Ubicacion{nombre:"${vector.ubicacion?.nombreUbicacion}"})-[c:Camino]-> (u2:Ubicacion{nombre:"${ubicacionDestino?.nombreUbicacion}"}) return(c.nombre)  """

        var result=   session.run(query)

        if(result.list().isEmpty()){
           return true
       }
               else {
           var camino = result.list().get(0)["(c.nombre)"]

           return vector.tipo.posiblesCaminos().contains(darTipo(camino.toString())).not()

       }
}}



