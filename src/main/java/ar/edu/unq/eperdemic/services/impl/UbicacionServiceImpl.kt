package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.DataDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import ar.edu.unq.eperdemic.utility.random.RandomMasterImpl

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
        val randomGenerator: RandomMaster = RandomMasterImpl()
        val ubicacion = this.recuperarUbicacion(nombreUbicacion)
        val vectoresInfectados = ubicacion.vectores.filter { vector -> vector.estado is Infectado }
        if (vectoresInfectados.isEmpty()) {
            return
        }
        // obtengo un vector infectado aleatoriamente
        val indiceAleatorio = randomGenerator.giveMeARandonNumberBeetween(1.0, vectoresInfectados.size as Double).toInt() - 1
        val vectorInfectadoAleatorio = vectoresInfectados.get(indiceAleatorio)
        val vectoresAContagiar = ubicacion.vectores.filter { vector -> vector.id != vectorInfectadoAleatorio.id }
        vectorService.contagiar(vectorInfectadoAleatorio, vectoresAContagiar)
    }
}