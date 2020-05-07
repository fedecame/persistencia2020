package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.hibernate.Transaction
import java.util.*


open class HibernateDAO<T>(private val entityType: Class<T>) {

    fun guardar(item: T) {
        val session = TransactionRunner.currentSession
        session.save(item)
    }

    fun recuperar(id: Long?): T {
        val session = TransactionRunner.currentSession
        return session.get(entityType, id)
    }

    fun recuperar(nombre : String?):T{
        val session = TransactionRunner.currentSession
        return session.get(entityType, nombre)
    }

    fun eliminar(item: T) {
        val session = TransactionRunner.currentSession
        session.delete(item)
    }

    fun actualizar(item: T){
        val session = TransactionRunner.currentSession
        session.saveOrUpdate(item)
    }
}