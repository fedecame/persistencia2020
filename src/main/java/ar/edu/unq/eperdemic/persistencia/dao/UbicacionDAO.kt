package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector

interface UbicacionDAO {
    fun crear (ubicacion: Ubicacion): Ubicacion
    fun recuperar(nombre:String): Ubicacion
    // fun recuperarAll():MutableList<Ubicacion>
    fun actualizar(ubicacion:Ubicacion)
    fun agregarVector(vector: Vector, ubicacion: Ubicacion)

    //Entrega Neo4j
    fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String)
    fun conectados(nombreDeUbicacion:String): List<Ubicacion>
    fun mover(vector: Vector, nombreUbicacion:String)
    fun capacidadDeExpansion(vectorId: Long, movimientos: Int) : Int
}