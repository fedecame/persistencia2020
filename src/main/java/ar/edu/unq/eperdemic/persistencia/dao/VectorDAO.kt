package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector

interface VectorDAO {
    fun crear(vector: Vector): Int
    fun recuperar(patogenoId: Int): Patogeno
}