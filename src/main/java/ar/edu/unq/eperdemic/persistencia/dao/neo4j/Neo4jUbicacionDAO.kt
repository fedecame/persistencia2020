package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.TipoCamino
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.CaminoNoSoportado
import ar.edu.unq.eperdemic.modelo.exception.UbicacionMuyLejana
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j


class Neo4jUbicacionDAO :UbicacionDaoNeo4j{
//    val session =DriverNeo4j().driver.session()

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
//        val session = TransactionNeo4j.currentSession
        val transaction = TransactionNeo4j.currentTransaction
        val query = """Match(ubicacionUno:Ubicacion {nombre:"$ubicacion1"})Match(ubicacionDos:Ubicacion{nombre:"$ubicacion2"}) MERGE (ubicacionUno)-[c:Camino {nombre:"$tipoCamino"}]->(ubicacionDos) """
        transaction.run(query)
    }


    override fun conectados(nombreDeUbicacion:String): List<Ubicacion>{
       val ubicacion= Ubicacion()
        ubicacion.nombreUbicacion="TibetDojo"

        val list= listOf(ubicacion)
        return list
    }



 override fun esAledaña(nombreDeUbicacion: String, uPosibleAledaña:String) {
     var transaction = TransactionNeo4j.currentTransaction
     var query = """ Match(u1:Ubicacion {nombre:"$nombreDeUbicacion"})-[c:Camino]->(u2:Ubicacion {nombre:"$uPosibleAledaña" }) return(c) """
     var result = transaction.run(query)
     if(result.list().isEmpty()){
         throw UbicacionMuyLejana()
     }
 }


private fun darTipo(camino:String) : TipoCamino? {
    when(camino){
        "Terrestre"-> return TipoCamino.Terrestre
        "Aereo"->return TipoCamino.Aereo
        "Maritimo"-> return TipoCamino.Maritimo
      else -> return null
  }

}
    override fun noEsCapazDeMoverPorCamino(vector: Vector, ubicacionDestino: String) {
         var transaction = TransactionNeo4j.currentTransaction
         var nombreUbicacionActual = vector.ubicacion?.nombreUbicacion
         var query = """ match(u1:Ubicacion{nombre:"$nombreUbicacionActual"})-[c:Camino {nombre:"Terrestre"}]-> (u2:Ubicacion{nombre:"$ubicacionDestino"}) return(c.nombre)  """
         var result = transaction.run(query).list()
         if (result.isEmpty()) {
             throw CaminoNoSoportado()
         }
     }
}



