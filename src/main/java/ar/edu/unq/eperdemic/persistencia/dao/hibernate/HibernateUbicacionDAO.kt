package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacionADondeSeDeseaMover
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO


class HibernateUbicacionDAO : HibernateDAO<Ubicacion>(Ubicacion::class.java), UbicacionDAO {

    override fun crear(ubicacion: Ubicacion): Ubicacion {
        super.guardar(ubicacion)
        return ubicacion
    }

    override fun recuperar(nombre: String): Ubicacion {
        var ubicacion=super.recuperar(nombre)
        if(ubicacion==null){
            throw NoExisteUbicacionADondeSeDeseaMover("Nelson")
        }
        return ubicacion
    }


}