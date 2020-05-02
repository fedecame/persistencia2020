package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO

class HibernateVectorDAO :  HibernateDAO<Vector>(Vector::class.java), VectorDAO  {

    override fun crear(vector : Vector): Vector {
        super.guardar(vector)
        print("El ID del vector acrear es: " + vector.id!!)
        return vector
    }

    override fun recuperar(vectorID: Int): Vector {
        try{
            return super.recuperar(vectorID.toLong())
        }
        catch (e : Exception){
            throw IDVectorNoEncontradoException(vectorID)
        }
    }

}
