package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import ar.edu.unq.eperdemic.utility.random.RandomMasterImpl

class UbicacionServiceImpl(var ubicacionDao: UbicacionDAO) : UbicacionService {
    var vectorService: VectorService = VectorServiceImpl(HibernateVectorDAO(), HibernateUbicacionDAO())
    var vectorDao=HibernateVectorDAO()
    var randomGenerator: RandomMaster = RandomMasterImpl()
    var neo4jUbicacionDAO=Neo4jUbicacionDAO()

    override fun recuperarUbicacion(nombreUbicacion: String):Ubicacion{
        return TransactionRunner.addHibernate().runTrx {
            ubicacionDao.recuperar(nombreUbicacion)
        }
    }

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        TransactionRunner.addNeo4j().runTrx {
            neo4jUbicacionDAO.conectar(ubicacion1, ubicacion2, tipoCamino)
        }
    }

    override fun capacidadDeExpansion(vectorId: Long, movimientos : Int): Int = TransactionRunner.addNeo4j().addHibernate().runTrx {  neo4jUbicacionDAO.capacidadDeExpansion(vectorId, movimientos)}

    override fun crearUbicacion(nombreUbicacion: String): Ubicacion {
        val ubicacion= Ubicacion()
        ubicacion.nombreUbicacion=nombreUbicacion
        return TransactionRunner.addHibernate().runTrx {
            neo4jUbicacionDAO.crear(ubicacion)
            ubicacionDao.crear(ubicacion)
        }

    }


    override fun mover(vectorId: Int, nombreUbicacion: String) {
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            var vector = vectorDao.recuperar(vectorId)
            neo4jUbicacionDAO.esAledaña(vector.ubicacion?.nombreUbicacion.toString(), nombreUbicacion)
            neo4jUbicacionDAO.noEsCapazDeMoverPorCamino(vector, nombreUbicacion)
            vectorService.mover(vectorId,nombreUbicacion)
            vector.ubicacion=ubicacionDao.recuperar(nombreUbicacion)//actualizo Ubicacion de Vector
            vectorDao.actualizar(vector)
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
}