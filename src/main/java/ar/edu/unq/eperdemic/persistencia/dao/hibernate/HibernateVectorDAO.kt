package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO

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

    override fun infectar(vector: Vector, especie: Especie): List<Pair<Vector, Especie>> {
        TODO("Not yet implemented")
    }


    /*
    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) : List<Pair<Vector, Especie>> {
        val infecciones: List<Pair<Vector, Especie>> = vectorInfectado.contagiar(vectores)

        for(vectorAContagiar in vectores){
            super.actualizar(vectorAContagiar)
        }
        return infecciones
    }
     */

    override fun borrar(vector: Vector) {
        super.eliminar(vector)
    }

}
