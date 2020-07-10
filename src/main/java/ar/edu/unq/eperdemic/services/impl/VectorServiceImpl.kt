package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.FeedService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class VectorServiceImpl(var vectorDao: VectorDAO, var ubicacionDao: UbicacionDAO, var feedService : FeedService = FeedServiceImpl(FeedMongoDAO())) : VectorService {

    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        val infecciones: List<Pair<Vector, Especie>> = vectorInfectado.contagiar(vectores)
        TransactionRunner.addHibernate().runTrx {
            for (vectorAContagiar in vectores) {
                vectorDao.actualizar(vectorAContagiar)
            }
        }
        this.fastForwardFeed(infecciones, vectorInfectado.id)
    }


    fun fastForwardFeed(infecciones: List<Pair<Vector, Especie>>, vectorInfectado: Long? = null){
        val especieDAO = HibernateEspecieDAO()
        TransactionRunner.addHibernate().runTrx {
            infecciones.forEach {
                val tipoPatogenoDeLaEspecie = it.second.patogeno.tipo
                val nombre_de_la_especie = it.second.nombre
                val ubicacion = it.first.ubicacion
                if (especieDAO.esPandemia(it.second) && !feedService.especieYaTieneEventoPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
                }
                if (!feedService.especieYaEstabaEnLaUbicacion(ubicacion!!.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie, it.first.id!!))
                } else {
                    feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorInfectado, it.first.id!!, ubicacion?.nombreUbicacion, nombre_de_la_especie))
                }
            }
        }
    }

    override fun infectar(vector: Vector, especie: Especie) {
        lateinit var infeccion : List<Pair<Vector, Especie>>
        TransactionRunner.addHibernate().runTrx {
            val especieDB = HibernateEspecieDAO().recuperarEspecie(especie.id!!)
            infeccion = vectorDao.infectar(vector,especieDB)
        }
        this.fastForwardFeed(infeccion)
    }

    override fun enfermedades(vectorId: Int): List<Especie> = TransactionRunner.addHibernate().runTrx { vectorDao.enfermedades(vectorId) }

    override fun crearVector(vector: Vector): Vector = TransactionRunner.addHibernate().runTrx {
        var vector1=vectorDao.crear(vector)
        vector1
    }

    override fun recuperarVector(vectorID: Int): Vector = TransactionRunner.addHibernate().runTrx { vectorDao.recuperar(vectorID) }

    override fun borrarVector(vectorId: Int) {
        TransactionRunner.addHibernate().runTrx {
            val vector = vectorDao.recuperar(vectorId)
            vectorDao.borrar(vector)
        }
    }
}