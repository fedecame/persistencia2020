package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.exception.ConectarMismaUbicacion
import ar.edu.unq.eperdemic.modelo.exception.MoverMismaUbicacion
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEstadisticasDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.utility.random.RandomMaster
import ar.edu.unq.eperdemic.utility.random.RandomMasterImpl

class UbicacionServiceImpl(var HibernateUbicacionDao: UbicacionDAO) : UbicacionService {
    var vectorDao = HibernateVectorDAO()
    var estadisticasDao= HibernateEstadisticasDAO()
    var vectorService: VectorService = VectorServiceImpl(vectorDao, HibernateUbicacionDAO())
    var randomGenerator: RandomMaster = RandomMasterImpl
    var neo4jUbicacionDAO=Neo4jUbicacionDAO()
    val feedService = FeedServiceImpl(FeedMongoDAO())

    override fun recuperarUbicacion(nombreUbicacion: String):Ubicacion{
        return TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDao.recuperar(nombreUbicacion)
        }
    }

    override fun conectar(ubicacion1: String, ubicacion2: String, tipoCamino: String) {
        if (ubicacion1 == ubicacion2) {
            throw ConectarMismaUbicacion()
        }
        var ubicacionUno= recuperarUbicacion(ubicacion1)
        var ubicacionDos= recuperarUbicacion(ubicacion2)


        TransactionRunner.addNeo4j().runTrx {
           // neo4jUbicacionDAO.conectar(ubicacion1, ubicacion2, tipoCamino)
            neo4jUbicacionDAO.conectar(ubicacionUno,ubicacionDos,tipoCamino)
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
        lateinit var ubicacionInicial : String
        lateinit var infeccionYUbicacion : Pair<List<Pair<Vector, Especie>>, List<Ubicacion>>
        val especieDAO = HibernateEspecieDAO()
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            HibernateUbicacionDao.recuperar(nombreUbicacion) // Valida que exista la ubicacion en la base de datos
            val vector = vectorDao.recuperar(vectorId)
            ubicacionInicial = vector.ubicacion!!.nombreUbicacion
            if (ubicacionInicial == nombreUbicacion) {
                throw MoverMismaUbicacion()
            }
            neo4jUbicacionDAO.esAledaña(ubicacionInicial, nombreUbicacion) // Cambiar el nombre del mensaje
            neo4jUbicacionDAO.noEsCapazDeMoverPorCamino(vector, nombreUbicacion) // Cambiar el nombre del mensaje
            infeccionYUbicacion = HibernateUbicacionDao.mover(vector, nombreUbicacion)
            // si se ejecuta esto es porque se movio y no exploto.

            infeccionYUbicacion.first.forEach {
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
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorId.toLong(), it.first.id!!, ubicacion?.nombreUbicacion))
            }
            feedService.agregarEvento(EventoFactory.eventoArribo(vectorId.toLong(), ubicacionInicial, nombreUbicacion))

            var vectoresEnUbicacion= HibernateUbicacionDao.recuperar(nombreUbicacion).vectores
            vectoresEnUbicacion.forEach { v->if( FeedServiceImpl(FeedMongoDAO()).vectorFueContagiadoAlMover(nombreUbicacion,vectorId,v.id?.toInt()!!))
                FeedServiceImpl(FeedMongoDAO()).agregarEvento(EventoFactory.eventoPorArriboYContagio(nombreUbicacion,vectorId.toLong(),v.id!!))
            }
        }

    }

    override fun expandir(nombreUbicacion: String) {
        val ubicacion = this.recuperarUbicacion(nombreUbicacion)
        val vectoresInfectados = ubicacion.vectores.filter { vector -> vector.estado is Infectado }
        if (vectoresInfectados.isEmpty()) {
            return
        }
        // obtengo un vector infectado aleatoriamente
        val indiceAleatorio = if (vectoresInfectados.size == 1) 0 else randomGenerator.giveMeARandonNumberBeetween(0.0, vectoresInfectados.size.toDouble()-1).toInt()
        val vectorInfectadoAleatorio = vectoresInfectados.get(indiceAleatorio)
        val vectoresAContagiar = ubicacion.vectores.filter { vector -> vector.id != vectorInfectadoAleatorio.id }

        var infecciones: List<Pair<Vector, Especie>> = listOf()
        val especieDAO = HibernateEspecieDAO()
        TransactionRunner.addHibernate().runTrx {
            infecciones = vectorDao.contagiar(vectorInfectadoAleatorio, vectoresAContagiar)
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
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorInfectadoAleatorio.id!!, it.first.id!!, ubicacion?.nombreUbicacion))
            }
        }
    }

    override fun moverMasCorto(vectorId: Long, nombreDeUbicacion: String) {
        var parInfeccionesUbicaciones : Pair<List<Pair<Vector, Especie>>, List<Ubicacion>> = Pair(listOf(), listOf())
        lateinit var vector : Vector
        val especieDAO = HibernateEspecieDAO()
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            vector = vectorDao.recuperar(vectorId.toInt())
            val ubicacion = HibernateUbicacionDao.recuperar(nombreDeUbicacion)
            parInfeccionesUbicaciones = neo4jUbicacionDAO.moverMasCorto(vector, ubicacion)
            // si se ejecuta esto es porque se movio y no exploto.

            parInfeccionesUbicaciones.first.forEach {
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
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorId, it.first.id!!, ubicacion?.nombreUbicacion))
            }
        }
        var ubicacionOrigenActual = vector.ubicacion!!.nombreUbicacion
        parInfeccionesUbicaciones.second.forEach {
            feedService.agregarEvento(EventoFactory.eventoArribo(vectorId, ubicacionOrigenActual, it.nombreUbicacion))
            ubicacionOrigenActual = it.nombreUbicacion
        }
    }

    override fun capacidadDeExpansion(vectorId: Long, movimientos: Int): Int {
        return TransactionRunner.addHibernate().addNeo4j().runTrx {  neo4jUbicacionDAO.capacidadDeExpansion(vectorId, movimientos) }
    }

}