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
import ar.edu.unq.eperdemic.services.runner.TransactionHibernate
import ar.edu.unq.eperdemic.services.runner.Transaction
import ar.edu.unq.eperdemic.services.runner.TransactionNeo4j
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import ar.edu.unq.eperdemic.utility.random.RandomMasterImpl

class UbicacionServiceImpl(var HibernateUbicacionDao: UbicacionDAO) : UbicacionService {
    var vectorDao = HibernateVectorDAO()
//    var vectorService: VectorService = VectorServiceImpl(vectorDao, HibernateUbicacionDAO())
    var randomGenerator: RandomMaster = RandomMasterImpl()
    var neo4jUbicacionDAO=Neo4jUbicacionDAO()

    override fun recuperarUbicacion(nombreUbicacion: String):Ubicacion{
        return TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDao.recuperar(nombreUbicacion)
        }
    }

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        TransactionRunner.addNeo4j().runTrx {
            neo4jUbicacionDAO.conectar(ubicacion1, ubicacion2, tipoCamino)
        }
    }

    override fun conectados(nombreDeUbicacion: String): List<Ubicacion> {
        return TransactionRunner.addNeo4j().runTrx {
            neo4jUbicacionDAO.conectados(nombreDeUbicacion)
        }
    }

    override fun crearUbicacion(nombreUbicacion: String): Ubicacion {
        val ubicacion= Ubicacion()
        ubicacion.nombreUbicacion=nombreUbicacion
        return TransactionRunner.addHibernate().addNeo4j().runTrx {
            neo4jUbicacionDAO.crear(ubicacion)
            HibernateUbicacionDao.crear(ubicacion)
        }
    }

    override fun mover(vectorId: Int, nombreUbicacion: String) {
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            var vector = vectorDao.recuperar(vectorId)
            neo4jUbicacionDAO.esAledaña(vector.ubicacion?.nombreUbicacion.toString(), nombreUbicacion) // Cambiar el nombre del mensaje
            neo4jUbicacionDAO.noEsCapazDeMoverPorCamino(vector, nombreUbicacion) // Cambiar el nombre del mensaje
            HibernateUbicacionDao.mover(vector, nombreUbicacion)


//            vectorService.mover(vectorId,nombreUbicacion)
//            vector.ubicacion=ubicacionDao.recuperar(nombreUbicacion)//actualizo Ubicacion de Vector
//            vectorDao.actualizar(vector)
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

        val vectorDao = HibernateVectorDAO()
        TransactionRunner.addHibernate().runTrx {
            vectorDao.contagiar(vectorInfectadoAleatorio, vectoresAContagiar)
        }
    }

    override fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String) {
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            val vector = vectorDao.recuperar(vectorId)
            val ubicacion = HibernateUbicacionDao.recuperar(nombreDeUbicacion)
            neo4jUbicacionDAO.moverMasCorto(vector, ubicacion)
        }
    }

    override fun capacidadDeExpansion(vectorId: Long, movimientos: Int): Int {
        return TransactionRunner.addHibernate().addNeo4j().runTrx {  neo4jUbicacionDAO.capacidadDeExpansion(vectorId, movimientos) }
    }

}