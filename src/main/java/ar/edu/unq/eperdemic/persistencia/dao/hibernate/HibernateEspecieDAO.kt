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
}