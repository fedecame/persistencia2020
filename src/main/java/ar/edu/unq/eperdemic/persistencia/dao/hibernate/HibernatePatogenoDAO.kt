package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.runner.TransactionHibernate
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernatePatogenoDAO  : HibernateDAO<Patogeno>(Patogeno::class.java), PatogenoDAO {

    override fun crear(patogeno: Patogeno): Int {
        super.guardar(patogeno)
        return patogeno.id!!
    }

    override fun recuperar(patogenoId: Int): Patogeno{
        val session = TransactionHibernate.currentSession
        val res = session!!.get(entityType, patogenoId)
        if (res === null){
            throw PatogenoNotFoundRunTimeException(patogenoId)
        }
        return res
    }

    override fun recuperarATodos(): List<Patogeno> {
        TODO("Not yet implemented")
    }

    override fun eliminarTodos() {
        TODO("Not yet implemented")
    }

}
