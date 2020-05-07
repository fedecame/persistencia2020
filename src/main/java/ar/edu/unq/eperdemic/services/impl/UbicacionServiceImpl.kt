package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class UbicacionServiceImpl(var ubicacionDao: UbicacionDAO, var dataDAO: DataDAO) : UbicacionService {
   var vectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateDataDAO())



    override fun crearUbicacion(nombreUbicacion: String): Ubicacion {
        var ubicacion= Ubicacion()
        ubicacion.nombreUbicacion=nombreUbicacion
        return TransactionRunner.runTrx {
            ubicacionDao.crear(ubicacion)

            ubicacionDao.recuperar(nombreUbicacion)
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


}