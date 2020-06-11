package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector

interface UbicacionDaoNeo4j {
    fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String)
    fun conectados(nombreDeUbicacion:String): List<Ubicacion>
    fun mover(vector: Vector, nombreUbicacion:String)




}