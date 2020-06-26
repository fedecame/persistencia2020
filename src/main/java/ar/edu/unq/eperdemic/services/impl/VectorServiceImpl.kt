package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.FeedService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class VectorServiceImpl(var vectorDao: VectorDAO, var ubicacionDao: UbicacionDAO, var feedService : FeedService = FeedServiceImpl(FeedMongoDAO())) : VectorService {
    private val eventoFactory = EventoFactory

    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        var infecciones: List<Pair<Vector, Especie>> = listOf()
        TransactionRunner.addHibernate().runTrx {
            infecciones = vectorDao.contagiar(vectorInfectado, vectores)
        }

        val especieDAO = HibernateEspecieDAO()
        TransactionRunner.addHibernate().runTrx {
            infecciones.forEach {
                val tipoPatogenoDeLaEspecie = it.second.patogeno.tipo
                val nombre_de_la_especie = it.second.nombre
                val ubicacion = it.first.ubicacion
                if (ubicacion !== null && !feedService.especieYaEstabaEnLaUbicacion(ubicacion.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie))
                }
//            val especieDB = especieDAO.recuperarEspecie(it.second.id!!)
                if (especieDAO.esPandemia(it.second)) { // agregar validacion de que sea la primera vez que es pandemia
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
                }
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorInfectado.id!!, it.first.id!!, ubicacion?.nombreUbicacion))
            }
        }

//        //Esto hay que ponerlo en otro lado?
//        val nombreUbicacion = vectorInfectado.ubicacion!!.nombreUbicacion
//        val especies = vectorInfectado.especies
//        especies.forEach {
//            val tipoPatogenoDeLaEspecie = it.patogeno.tipo
//            val nombre_de_la_especie = it.nombre
//            if (!feedService.especieYaEstabaEnLaUbicacion(nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
//                feedService.agregarEvento(eventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, nombreUbicacion, nombre_de_la_especie))
//            }
//        }
    }

    override fun infectar(vector: Vector, especie: Especie) {
        var infeccion : List<Pair<Vector, Especie>> = listOf()
        val especieDAO = HibernateEspecieDAO()
        TransactionRunner.addHibernate().runTrx {
            val especieDB = HibernateEspecieDAO().recuperarEspecie(especie.id!!)
            infeccion = vectorDao.infectar(vector,especieDB)
        }

        TransactionRunner.addHibernate().runTrx {
            if (infeccion.size > 0) {
                val tipoPatogenoDeLaEspecie = infeccion.first().second.patogeno.tipo
                val nombre_de_la_especie = infeccion.first().second.nombre
                val ubicacion = infeccion.first().first.ubicacion
                if (ubicacion !== null && !feedService.especieYaEstabaEnLaUbicacion(ubicacion.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie))
                }
//            val especieDB = especieDAO.recuperarEspecie(infeccion.first().second.id!!)
                if (especieDAO.esPandemia(infeccion.first().second)) { // agregar validacion de que sea la primera vez que es pandemia
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
                }
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(null, infeccion.first().first.id!!, ubicacion?.nombreUbicacion))
            }
        }



//        val tipoPatogenoDeLaEspecie = especie.patogeno.tipo
//        val nombre_de_la_especie = especie.nombre
//        val patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
//        if(patogenoService.esPandemia(especie.id!!)){
//            feedService.agregarEvento(eventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
//        }
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