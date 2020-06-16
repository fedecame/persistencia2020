package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class VectorServiceImpl(var vectorDao: VectorDAO, var ubicacionDao: UbicacionDAO) : VectorService {


    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        TransactionRunner.addHibernate().runTrx { vectorDao.contagiar(vectorInfectado, vectores) }
    }

    override fun infectar(vector: Vector, especie: Especie) {
        TransactionRunner.addHibernate().runTrx { vectorDao.infectar(vector,especie) }    }

//    override fun mover(vectorId: Int, nombreUbicacion: String) {
//
//            var vector= vectorDao.recuperar(vectorId)
//            var ubicacionOrigen=ubicacionDao.recuperar(vector.ubicacion?.nombreUbicacion!!)
//            vector.ubicacion=ubicacionDao.recuperar(nombreUbicacion)//actualizo Ubicacion de Vector
//            vectorDao.actualizar(vector)
//        }

    override fun enfermedades(vectorId: Int): List<Especie> = TransactionRunner.addHibernate().runTrx { vectorDao.enfermedades(vectorId) }

    override fun crearVector(vector: Vector): Vector = TransactionRunner.addHibernate().runTrx {
        var vector1=vectorDao.crear(vector)
        vector1
    }

    override fun recuperarVector(vectorID: Int): Vector = TransactionRunner.addHibernate().runTrx { vectorDao.recuperar(vectorID) }

    override fun borrarVector(vectorId: Int) {
        TransactionRunner.addHibernate().runTrx {
            val vector = vectorDao.recuperar(vectorId)
            vectorDao.borrar(vector)
        }
    }
}