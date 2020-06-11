package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.NoExisteUbicacion
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionHibernate
import ar.edu.unq.eperdemic.services.runner.TransactionRunner


class HibernateUbicacionDAO : HibernateDAO<Ubicacion>(Ubicacion::class.java), UbicacionDAO {

    override fun crear(ubicacion: Ubicacion): Ubicacion {
        super.guardar(ubicacion)
        return ubicacion
    }

    private fun myRecuperar(nombre : String?):Ubicacion?{
    val session = TransactionHibernate.currentSession
        return session!!.get(entityType, nombre)
    }

    override fun recuperar(nombre: String): Ubicacion {
        var ubicacion = this.myRecuperar(nombre)
        if (ubicacion === null) {
            throw NoExisteUbicacion(nombre)
        }
        return ubicacion
    }

    override fun agregarVector(vector: Vector, ubicacion: Ubicacion) {
        val ubicacionDB = this.recuperar(ubicacion.nombreUbicacion)
        ubicacionDB.agregarVector(vector)
        this.actualizar(ubicacionDB)
    }

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        TODO("Not yet implemented")
    }

    override fun conectados(nombreDeUbicacion: String): List<Ubicacion> {
        TODO("Not yet implemented")
    }

    override fun mover(vector: Vector, nombreUbicacion: String) {
        TODO("Not yet implemented")
    }

    override fun capacidadDeExpansion(vectorId: Long, movimientos: Int): Int {
        //Esto no deberia ser implementado en Hibernate
        return 0
    }

}





