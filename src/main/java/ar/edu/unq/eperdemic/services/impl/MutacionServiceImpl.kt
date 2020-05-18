package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx

class MutacionServiceImpl(var mutacionDao : MutacionDAO, var dataDao : DataDAO, val patogenoService: PatogenoService) : MutacionService{
    override fun mutar(especieId: Int, mutacionId: Int) {
        runTrx {
            val mutacion = mutacionDao.recuperar(mutacionId)
            val especie = patogenoService.recuperarEspecie(especieId)

            especie.mutar(mutacion)
            patogenoService.actualizarEspecie(especie)
        }
    }

    override fun crearMutacion(mutacion: Mutacion): Mutacion = runTrx {
        var mutacion1 = mutacionDao.crear(mutacion)
        mutacion1
    }

    override fun recuperarMutacion(mutacionId: Int): Mutacion = runTrx { mutacionDao.recuperar(mutacionId) }
}