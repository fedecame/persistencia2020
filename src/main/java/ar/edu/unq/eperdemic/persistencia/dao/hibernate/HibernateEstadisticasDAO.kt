package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernateEstadisticasDAO : EstadisticasDAO {

    override fun vectoresPresentes(nombreUbicacion: String): Int {
        val hql = "SELECT COUNT (*) FROM Vector v WHERE v.ubicacion = :nombreUbicacion"
        val session = TransactionRunner.currentSession
        val query = session.createQuery(hql, Long::class.javaObjectType )
        query.setParameter("nombreUbicacion", nombreUbicacion);
        return query.firstResult.toInt()
    }

    override fun vectoresInfectados(nombreUbicacion: String): Int {
        return 1
    }

    override fun especieQueInfectaAMasVectoresEn(nombreUbicacion: String): String {
        return ""
    }
}