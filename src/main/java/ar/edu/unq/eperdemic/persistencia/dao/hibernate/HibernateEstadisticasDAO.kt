package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticasDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano

class HibernateEstadisticasDAO : EstadisticasDAO {

    override fun vectoresPresentes(nombreUbicacion: String): Int {
        val hql = "SELECT COUNT (*) FROM Vector v WHERE v.ubicacion.nombreUbicacion =:nombreUbicacion"
        val session = TransactionRunner.currentSession
        val query = session.createQuery(hql, Long::class.javaObjectType)
        query.setParameter("nombreUbicacion", nombreUbicacion)
        return query.singleResult.toInt()
    }

    override fun vectoresInfectados(nombreUbicacion: String): Int {
        val hql = "SELECT COUNT (*) FROM Vector v WHERE v.ubicacion.nombreUbicacion =:nombreUbicacion AND v.estado =:infectado"
        val session = TransactionRunner.currentSession
        val query = session.createQuery(hql, Long::class.javaObjectType)
        query.setParameter("nombreUbicacion", nombreUbicacion)
        val estado = Infectado()
        query.setParameter("infectado", estado)
        return query.singleResult.toInt()
    }

    override fun especieQueInfectaAMasVectoresEn(nombreUbicacion: String): String {
        return ""
    }

    override fun especieLider(): Especie {
        val session = TransactionRunner.currentSession
        val hql = "SELECT v FROM Vector v WHERE v.estado=:estado AND v.tipo=:tipo"

        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("estado", Infectado())
        query.setParameter("tipo",Humano() )


        return Especie()
    }
}