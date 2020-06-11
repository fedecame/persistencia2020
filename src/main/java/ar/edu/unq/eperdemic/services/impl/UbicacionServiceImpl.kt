package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import ar.edu.unq.eperdemic.utility.random.RandomMasterImpl

class UbicacionServiceImpl(var ubicacionDao: UbicacionDAO) : UbicacionService {
    var vectorDao = HibernateVectorDAO()
//    var vectorService: VectorService = VectorServiceImpl(vectorDao, HibernateUbicacionDAO())
    var randomGenerator: RandomMaster = RandomMasterImpl()
    var neo4jUbicacionDAO=Neo4jUbicacionDAO()
    override fun recuperarUbicacion(nombreUbicacion: String):Ubicacion{
        return TransactionRunner.addHibernate().runTrx {
            ubicacionDao.recuperar(nombreUbicacion)
        }
    }

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        TODO("Not yet implemented")
    }

    override fun crearUbicacion(nombreUbicacion: String): Ubicacion {
        val ubicacion= Ubicacion()
        ubicacion.nombreUbicacion=nombreUbicacion
        return TransactionRunner.addHibernate().runTrx {
            ubicacionDao.crear(ubicacion)
        }
    }
    override fun mover(vectorId: Int, nombreUbicacion: String) {
        TransactionRunner.addHibernate().addNeo4j().runTrx {
//            vectorService.mover(vectorId, nombreUbicacion)
//            ubicacionDao.recuperar(nombreUbicacion)
            var vector= vectorDao.recuperar(vectorId)
//            var ubicacionOrigen=ubicacionDao.recuperar(vector.ubicacion?.nombreUbicacion!!)
            vector.ubicacion=ubicacionDao.recuperar(nombreUbicacion)//actualizo Ubicacion de Vector
            vectorDao.actualizar(vector)
          
//            var vectorAMover= vectorService.recuperarVector(vectorId)

//            neo4jUbicacionDAO.mover(vectorAMover, nombreUbicacion)
        }

    }

    override fun expandir(nombreUbicacion: String) {
        val ubicacion = this.recuperarUbicacion(nombreUbicacion)
        val vectoresInfectados = ubicacion.vectores.filter { vector -> vector.estado is Infectado }
        if (vectoresInfectados.isEmpty()) {
            return
        }
        // obtengo un vector infectado aleatoriamente
        val indiceAleatorio = randomGenerator.giveMeARandonNumberBeetween(0.0, vectoresInfectados.size.toDouble()-1).toInt()
        val vectorInfectadoAleatorio = vectoresInfectados.get(indiceAleatorio)
        val vectoresAContagiar = ubicacion.vectores.filter { vector -> vector.id != vectorInfectadoAleatorio.id }
//        vectorService.contagiar(vectorInfectadoAleatorio, vectoresAContagiar)

        TransactionRunner.addHibernate().runTrx {
            vectorDao.contagiar(vectorInfectadoAleatorio, vectoresAContagiar)
        }
    }

    override fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String) {
        lateinit var vector: Vector
        TransactionRunner.addHibernate().runTrx {
            vector = vectorDao.recuperar(vectorId)
        }

        TransactionRunner.addNeo4j().runTrx {
            val ubicacion = ubicacionDao.recuperar(nombreDeUbicacion)
            ubicacionDao.moverMasCorto(vector, ubicacion)
        }

        TODO("Not yet implemented")
    }
}