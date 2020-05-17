package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernateEstadisticasDAO : EstadisticasDAO {

    override fun vectoresPresentes(nombreUbicacion: String): Int {
        val hql = "SELECT COUNT (*) FROM Vector v WHERE v.ubicacion.nombreUbicacion =:nombreUbicacion"
        val session = TransactionRunner.currentSession
        val query = session.createQuery(hql, Long::class.javaObjectType)
        query.setParameter("nombreUbicacion", nombreUbicacion)
        return query.singleResult.toInt()
    }

    override fun vectoresInfectados(nombreUbicacion: String): Int {
        val hql = "SELECT COUNT (*) FROM Vector v WHERE v.ubicacion.nombreUbicacion =:nombreUbicacion AND v.estaInfectado()"
        val session = TransactionRunner.currentSession
        val query = session.createQuery(hql, Long::class.javaObjectType)
        query.setParameter("nombreUbicacion", nombreUbicacion)
        return query.singleResult.toInt()
    }

    override fun especieQueInfectaAMasVectoresEn(nombreUbicacion: String): String {
        return ""
    }
}