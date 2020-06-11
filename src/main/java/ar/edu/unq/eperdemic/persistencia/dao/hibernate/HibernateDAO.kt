package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.services.runner.TransactionHibernate

open class HibernateDAO<T>(val entityType: Class<T>) {

    fun guardar(item: T) {
        val session = TransactionHibernate.currentSession
        session.save(item)
    }

    fun recuperar(id: Long?): T {
        val session = TransactionHibernate.currentSession
        return session.get(entityType, id)
    }

    fun eliminar(item: T) {
        val session = TransactionHibernate.currentSession
        session.delete(item)
    }

    open fun actualizar(item: T){
        val session = TransactionHibernate.currentSession
        session.saveOrUpdate(item)
    }
}