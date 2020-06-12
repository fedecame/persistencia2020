package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class PatogenoServiceImpl(var patogenoDAO: PatogenoDAO, var especieDAO : EspecieDAO) : PatogenoService {

    override fun crearPatogeno(patogeno: Patogeno): Int {
        return TransactionRunner.addHibernate().runTrx { patogenoDAO.crear(patogeno) }
    }

    override fun recuperarPatogeno(id: Int): Patogeno {
        return TransactionRunner.addHibernate().runTrx { patogenoDAO.recuperar(id) }
    }

    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return TransactionRunner.addHibernate().runTrx { patogenoDAO.recuperarATodos() }
    }

    override fun agregarEspecie(idPatogeno: Int, nombreEspecie: String, paisDeOrigen: String): Especie =
            TransactionRunner.addHibernate().runTrx {
                val patogenoAAgregarEspecie = patogenoDAO.recuperar(idPatogeno)
                val especieResultado = patogenoAAgregarEspecie.crearEspecie(nombreEspecie, paisDeOrigen)
                especieDAO.crearEspecie(especieResultado)
                patogenoDAO.actualizar(patogenoAAgregarEspecie)
                especieResultado
            }

    override fun crearEspecie(especie: Especie) : Int {
        return TransactionRunner.addHibernate().runTrx { especieDAO.crearEspecie(especie) }
    }

    override fun recuperarEspecie(especieId: Int): Especie {
        return TransactionRunner.addHibernate().runTrx { especieDAO.recuperarEspecie(especieId) }
    }

    override fun cantidadDeInfectados(especieId: Int): Int {
        val especieDB = this.recuperarEspecie(especieId)
        return TransactionRunner.addHibernate().runTrx { especieDAO.cantidadDeInfectados(especieDB) }
    }

    override fun esPandemia(especieId: Int): Boolean {
        val especieDB = this.recuperarEspecie(especieId)
        return TransactionRunner.addHibernate().runTrx { especieDAO.esPandemia(especieDB) }
    }

    override fun actualizarEspecie(especie: Especie) {
        TransactionRunner.addHibernate().runTrx {
            especieDAO.actualizar(especie)
        }
    }
}
