package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.exception.IDMutacionNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO

class HibernateMutacionDAO : HibernateDAO<Mutacion>(Mutacion::class.java), MutacionDAO {

    override fun crear(mutacion: Mutacion): Mutacion {
        super.guardar(mutacion)
        return mutacion
    }

    override fun recuperar(mutacionId: Int): Mutacion {

        val mutacion = super.recuperar(mutacionId.toLong())

        if(mutacion == null){
            throw IDMutacionNoEncontradoException(mutacionId)
        }
        return mutacion
    }
}