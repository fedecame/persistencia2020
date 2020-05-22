package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.EspecieNotFoundRunTimeException
import ar.edu.unq.eperdemic.modelo.exception.PatogenoNotFoundRunTimeException
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernateEspecieDAO : HibernateDAO<Especie>(Especie::class.java), EspecieDAO {

    override fun crearEspecie(especie : Especie) : Int{
        super.guardar(especie)
        return especie.id!!
    }
    override fun recuperarEspecie(especieId: Int): Especie {
        val session = TransactionRunner.currentSession
        val res = session.get(entityType, especieId)
        if (res === null){
            throw EspecieNotFoundRunTimeException(especieId)
        }
        return res
    }

    override fun cantidadDeInfectados(especie: Especie): Int {
        val sql = """
                SELECT 
                    COUNT(id) AS cantidad_infectados
                FROM
                    Vector
                        INNER JOIN
                    Vector_Especie ON Vector.id = Vector_Especie.Vector_id
                WHERE
                    especies_id =:especieId
            """.trimIndent()
        val session = TransactionRunner.currentSession
        val query = session.createNativeQuery(sql)
        query.setParameter("especieId", especie.id)
        return query.singleResult.toString().toInt()
    }

    override fun esPandemia(especie: Especie): Boolean {
        val sql1 = """
                SELECT 
                    COUNT(DISTINCT ubicacion_nombreUbicacion) AS cantidad_ubicaciones_infectadas
                FROM
                    Vector
                        INNER JOIN
                    Vector_Especie ON Vector.id = Vector_Especie.Vector_id
                WHERE
                    especies_id =:especieId
            """.trimIndent()
        val sql2 = "SELECT COUNT(*) AS cantidad_ubicaciones FROM Ubicacion"
        val session = TransactionRunner.currentSession

        val query1 = session.createNativeQuery(sql1)
        query1.setParameter("especieId", especie.id)
        val cantUbicacionesInfectadas = query1.singleResult.toString().toInt()

        val query2 = session.createNativeQuery(sql2)
        val cantUbicacionesTotales = query2.singleResult.toString().toInt()

        return cantUbicacionesInfectadas > cantUbicacionesTotales.div(2)
    }
}