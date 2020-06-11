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
    var vectorService: VectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
    var randomGenerator: RandomMaster = RandomMasterImpl()
    var neo4jUbicacionDAO=Neo4jUbicacionDAO()
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
        }
    }
    override fun mover(vectorId: Int, nombreUbicacion: String) {
        TransactionRunner.runTrx {
            vectorService.mover(vectorId, nombreUbicacion)
            var vectorAMover= vectorService.recuperarVector(vectorId)

                neo4jUbicacionDAO.mover(vectorAMover, nombreUbicacion)

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
        vectorService.contagiar(vectorInfectadoAleatorio, vectoresAContagiar)
    }
}