package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class HibernatePatogenoServiceImpl(var patogenoDAO: PatogenoDAO, var especieDAO : EspecieDAO) : PatogenoService {

    override fun crearPatogeno(patogeno: Patogeno): Int {
        return runTrx { patogenoDAO.crear(patogeno) }
    }

    override fun recuperarPatogeno(id: Int): Patogeno {
        return runTrx { patogenoDAO.recuperar(id) }
    }

    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return runTrx { patogenoDAO.recuperarATodos() }
    }

    override fun agregarEspecie(id: Int, nombreEspecie: String, paisDeOrigen: String): Especie =
            runTrx {
                val patogenoAAgregarEspecie = patogenoDAO.recuperar(id)
                val especieResultado = patogenoAAgregarEspecie.crearEspecie(nombreEspecie, paisDeOrigen)
                especieDAO.crearEspecie(especieResultado)
                patogenoDAO.actualizar(patogenoAAgregarEspecie)
                especieResultado
            }

    override fun cantidadDeInfectados(especieId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun esPandemia(especieId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun recuperarEspecie(id: Int): Especie {
        TODO("Not yet implemented")
    }
}
