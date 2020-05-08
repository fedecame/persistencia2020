package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class UbicacionServiceImpl(var ubicacionDao: UbicacionDAO, var dataDAO: DataDAO) : UbicacionService {
   var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO(), HibernateUbicacionDAO())

    override fun recuperarUbicacion(nombreUbicacion: String):Ubicacion{
        return TransactionRunner.runTrx {
            ubicacionDao.recuperar(nombreUbicacion)
        }
    }

    override fun crearUbicacion(nombreUbicacion: String): Ubicacion {
        val ubicacion= Ubicacion()
        ubicacion.nombreUbicacion=nombreUbicacion
        return TransactionRunner.runTrx {
            ubicacionDao.crear(ubicacion)

            //ubicacionDao.recuperar(nombreUbicacion)
        }
    }
    override fun mover(vectorId: Int, nombreUbicacion: String) {

        TransactionRunner.runTrx {
            vectorService.mover(vectorId, nombreUbicacion)
        }
    }

    override fun expandir(nombreUbicacion: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun borrarTodo() {
        TransactionRunner.runTrx { dataDAO.clear() }
    }
}