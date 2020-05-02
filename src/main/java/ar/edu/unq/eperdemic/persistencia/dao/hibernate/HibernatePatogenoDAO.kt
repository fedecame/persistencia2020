package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO

open class HibernatePatogenoDAO : HibernateDAO<Patogeno>(Patogeno::class.java), PatogenoDAO  {
    override fun crear(patogeno: Patogeno): Int {
        TODO("Not yet implemented")
    }

    override fun actualizar(patogeno: Patogeno) {
        TODO("Not yet implemented")
    }

    override fun recuperar(patogenoId: Int): Patogeno {
        TODO("Not yet implemented")
    }

    override fun recuperarATodos(): List<Patogeno> {
        TODO("Not yet implemented")
    }

    override fun eliminarTodos() {
        TODO("Not yet implemented")
    }
}