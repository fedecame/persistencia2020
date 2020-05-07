package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO

class HibernateVectorDAO :  HibernateDAO<Vector>(Vector::class.java), VectorDAO  {

    override fun crear(vector : Vector): Vector {
        super.guardar(vector)
        return vector
    }

    override fun recuperar(vectorID: Int): Vector {
            return super.recuperar(vectorID.toLong())
    }

    override fun enfermedades(vectorID: Int): List<Especie> {
        val vector = this.recuperar(vectorID)
        val res = vector.especies
        return res.toList()
    }
}
