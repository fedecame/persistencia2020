package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Patogeno

interface PatogenoDAO {
    fun crear(patogeno: Patogeno): Int
    fun actualizar(patogeno: Patogeno )
    fun recuperar(patogenoId: Int): Patogeno
    fun recuperarATodos() : List<Patogeno>
    fun eliminarTodos()
}