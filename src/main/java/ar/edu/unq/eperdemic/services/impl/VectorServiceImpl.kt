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
            var vector= vectorDao.recuperar(vectorId)
            var ubicacionOrigen=ubicacionDao.recuperar(vector.ubicacion?.nombreUbicacion!!)
            vector.ubicacion=ubicacionDao.recuperar(nombreUbicacion)//actualizo Ubicacion de Vector
            ubicacionOrigen.vectores.removeIf { vector-> vector.id==vectorId.toLong() }//"desalojo" a el vector de la ubicacion origen
            ubicacionDao.actualizar(ubicacionOrigen)
            vectorDao.actualizar(vector)
        }}

    override fun enfermedades(vectorId: Int): List<Especie> = runTrx { vectorDao.enfermedades(vectorId) }

    override fun crearVector(vector: Vector): Vector = runTrx {
        var vector1=vectorDao.crear(vector)
        vector
    }

    override fun recuperarVector(vectorID: Int): Vector = runTrx { vectorDao.recuperar(vectorID) }

    override fun borrarVector(vectorId: Int) {
        runTrx {
            val vector = vectorDao.recuperar(vectorId)
            vectorDao.borrar(vector)
        }
    }
}