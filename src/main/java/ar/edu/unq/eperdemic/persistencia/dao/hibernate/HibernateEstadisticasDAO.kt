package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
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

   override fun lideres(): MutableList<Especie> {
        val session = TransactionRunner.currentSession
        val hql = "select e  " +
                "from Especie e INNER JOIN Vector v ON e.id = v.id " +
                "where v.tipo=?1 or v.tipo=?2 " +
                "group by e.id " +
                "order by count(*)  "
        val query = session.createQuery(hql, Especie::class.java)
        var tipo="Animal"
        var tipo2="Humano"
        query.setString(1,tipo)
        query.setString(2,tipo2)
        query.setMaxResults(10)
        return query.resultList
    }


}