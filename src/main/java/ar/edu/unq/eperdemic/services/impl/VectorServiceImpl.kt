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

// A FUTURO: Crear un MegalodonService que maneje todos los hilos y delegue en los otros services.
class VectorServiceImpl(var vectorDao: VectorDAO, var dataDAO: DataDAO, var ubicacionDao: UbicacionDAO) : VectorService {


    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        runTrx { vectorDao.contagiar(vectorInfectado, vectores) }
    }

    override fun infectar(vector: Vector, especie: Especie) {
        runTrx { vectorDao.infectar(vector,especie) }    }

    override fun mover(vectorId: Int, nombreUbicacion: String) {
        return runTrx {
            val vector= vectorDao.recuperar(vectorId)
            vector.ubicacion=ubicacionDao.recuperar(nombreUbicacion)
//            vectorDao.crear(vector)
            vectorDao.refresh(vector)
        }
    }

    override fun enfermedades(vectorId: Int): List<Especie> = runTrx { vectorDao.enfermedades(vectorId) }

    override fun crearVector(vector: Vector): Vector = runTrx {
        //HORA: Fijate de cambiar el nombre del parametro o de la variable..no se cual te esta tomando por defecto en las referencias.

        val vector=vectorDao.crear(vector)
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