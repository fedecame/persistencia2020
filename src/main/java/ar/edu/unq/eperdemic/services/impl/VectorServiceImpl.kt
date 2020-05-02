package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.VectorService

class VectorServiceImpl(private var vectorDao: VectorDAO) : VectorService {
    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        TODO("Not yet implemented")
    }

    override fun infectar(vector: Vector, especie: Especie) {
        TODO("Not yet implemented")
    }

    override fun enfermedades(vectorId: Int): List<Especie> {
        TODO("Not yet implemented")
    }

    override fun crearVector(vector: Vector): Int {
        return vectorDao.crear(vector)!!
    }

    override fun recuperarVector(vectorId: Int): Vector {
        TODO("Not yet implemented")
    }

    override fun borrarVector(vectorId: Int) {
        TODO("Not yet implemented")
    }
}