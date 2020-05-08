package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernateVectorDAO :  HibernateDAO<Vector>(Vector::class.java), VectorDAO  {

    override fun crear(vector : Vector): Vector {
        super.guardar(vector)
        return vector
    }
    override fun actualizar(vector:Vector){
       return super.actualizar(vector)
    }

    override fun recuperar(vectorID: Int): Vector {

        val vector = super.recuperar(vectorID.toLong())

        if(vector==null){
            throw IDVectorNoEncontradoException(vectorID)
        }
        return vector

    }

    override fun enfermedades(vectorID: Int): List<Especie> {
        val vector = this.recuperar(vectorID)
        val res = vector.especies
        return res.toList()
    }

    override fun infectar(vector: Vector, especie: Especie){
        val _vector = this.recuperar(vector.id)
        _vector.infectarse(especie)
        super.actualizar(_vector)
    }

    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        val _vector = this.recuperar(vectorInfectado.id)
        _vector.contagiar(vectores)
        for(vectorAContagiar in vectores){
            super.actualizar(vectorAContagiar)
        }

    }

    override fun borrar(vector: Vector) {
        super.eliminar(vector)
    }

}
