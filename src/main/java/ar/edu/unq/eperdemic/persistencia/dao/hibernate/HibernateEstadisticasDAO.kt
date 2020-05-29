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
        val q1 = "(SELECT e.nombre, COUNT(*) AS occ FROM Especie e INNER JOIN Vector_Especie ve ON e.id = ve.especies_id AND ve.Vector_id IN (SELECT v.id FROM Vector v WHERE v.ubicacion_nombreUbicacion=:nombreUbicacion) GROUP BY e.id ORDER BY occ DESC LIMIT 1) AS q;"
        val hql = "SELECT nombre FROM " + q1
        val session = TransactionRunner.currentSession
        val query = session.createNativeQuery(hql)
        query.setParameter("nombreUbicacion", nombreUbicacion)
        return query.singleResult.toString()
    }

    override fun especieLider(): Especie {
        val session = TransactionRunner.currentSession
        val hql = """
           select id,cantidadInfectaddosParaADN,nombre,paisDeOrigen,patogeno_id
           from Especie 
           inner join 
           (select count(comb.especies_id) as cantidad_humanos_contagiados,comb.especies_id
           from Vector_Especie as comb
           inner join Vector on
           Vector.id = comb.Vector_id
           where Vector.tipo = 'Humano'
           group by comb.especies_id
           order by cantidad_humanos_contagiados desc
           limit 1
           ) as temp on
           Especie.id = temp.especies_id
        """.trimIndent()

        val query = session.createNativeQuery(hql, Especie::class.java)

        return query.singleResult as Especie::class.java
    }

   override fun lideres(): MutableList<Especie> {
        val session = TransactionRunner.currentSession
        val hql = """
           select id,cantidadInfectadosParaADN,nombre,paisDeOrigen,patogeno_id
           from Especie 
           inner join 
           (select count(comb.especies_id) as cantidad_humanos_contagiados,comb.especies_id
           from Vector_Especie as comb
           inner join Vector on
           Vector.id = comb.Vector_id
           where Vector.tipo = 'Humano' OR Vector.tipo = "Animal"
           group by comb.especies_id
           order by cantidad_humanos_contagiados desc
           ) as temp on
           Especie.id = temp.especies_id
        """.trimIndent()
        val query = session.createNativeQuery(hql, Especie::class.java)
        query.setMaxResults(10)
        return query.resultList as MutableList<Especie>
    }


}