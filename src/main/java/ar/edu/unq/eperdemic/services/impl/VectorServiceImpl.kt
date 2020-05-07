package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx


class VectorServiceImpl(var vectorDao: VectorDAO, var dataDAO: DataDAO) : VectorService {
var ubicacionDao= HibernateUbicacionDAO()

    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        runTrx { vectorDao.contagiar(vectorInfectado, vectores) }
    }

    override fun infectar(vector: Vector, especie: Especie) {
        runTrx { vectorDao.infectar(vector,especie) }    }

    override fun mover(vectorId: Int, nombreUbicacion: String) {
        return runTrx {
            var vector= vectorDao.recuperar(vectorId)
            vector.ubicacion=ubicacionDao.recuperar(nombreUbicacion)
            vectorDao.crear(vector)
        }}

    override fun enfermedades(vectorId: Int): List<Especie> = runTrx { vectorDao.enfermedades(vectorId) }

    override fun crearVector(vector: Vector): Vector = runTrx {
        var vector=vectorDao.crear(vector)
        vector.ubicacion?.alojarVector(vector)
    vector
    }

    override fun recuperarVector(vectorID: Int): Vector = runTrx { vectorDao.recuperar(vectorID) }

    override fun borrarVector(vectorId: Int) {
        TODO("Not yet implemented")
    }

    override fun borrarTodo() {
            runTrx { dataDAO.clear() }
    }
}