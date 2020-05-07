package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO


class HibernateUbicacionDAO : HibernateDAO<Ubicacion>(Ubicacion::class.java), UbicacionDAO {

    override fun crear(ubicacion: Ubicacion): Ubicacion {
        super.guardar(ubicacion)
        return ubicacion
    }

    override fun recuperar(nombre: String): Ubicacion {
        return super.recuperar(nombre)    }

    override fun actualizar(ubicacion: Ubicacion): Ubicacion {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }






}