package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorDAO {
   fun crear (vector: Vector): Vector
   fun recuperar(vectorID: Int): Vector
   fun enfermedades(vectorID : Int) : List<Especie>
   fun infectar(vector: Vector, especie: Especie) : List<Pair<Vector, Especie>>
   fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) : List<Pair<Vector, Especie>>
   fun borrar(vector: Vector)
   fun actualizar(vector:Vector)
}