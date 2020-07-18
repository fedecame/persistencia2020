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
        return TransactionRunner.addNeo4j().addHibernate().runTrx {
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
        lateinit var nombreUbicacionInicial : String
        lateinit var infeccionYUbicacion : Pair<List<Pair<Vector, Especie>>, List<Ubicacion>>
        val especieDAO = HibernateEspecieDAO()
        TransactionRunner.addHibernate().addNeo4j().runTrx {
            val ubicacionNueva = HibernateUbicacionDao.recuperar(nombreUbicacion) // Valida que exista la ubicacion en la base de datos
            val vector = vectorDao.recuperar(vectorId)
            val ubicacionOrigen = vector.ubicacion!! // recupero la ubicacion original
            nombreUbicacionInicial = ubicacionOrigen.nombreUbicacion
            if (nombreUbicacionInicial == nombreUbicacion) {
                throw MoverMismaUbicacion()
            }
            neo4jUbicacionDAO.esAledaña(nombreUbicacionInicial, nombreUbicacion) // Cambiar el nombre del mensaje
            neo4jUbicacionDAO.noEsCapazDeMoverPorCamino(vector, nombreUbicacion) // Cambiar el nombre del mensaje
            infeccionYUbicacion = this.moverSubTarea(vector, ubicacionNueva)
            // si se ejecuta esto es porque se movio y no exploto.

            infeccionYUbicacion.first.forEach {
                val tipoPatogenoDeLaEspecie = it.second.patogeno.tipo
                val nombre_de_la_especie = it.second.nombre
                val ubicacion = it.first.ubicacion


                if (ubicacion !== null && !feedService.especieYaEstabaEnLaUbicacion(ubicacion.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie, it.first.id!!))
                }
//            val especieDB = especieDAO.recuperarEspecie(it.second.id!!)
                if (especieDAO.esPandemia(it.second)) { // agregar validacion de que sea la primera vez que es pandemia
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
                }
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorId.toLong(), it.first.id!!, ubicacion?.nombreUbicacion, nombre_de_la_especie))
            }
            feedService.agregarEvento(EventoFactory.eventoArribo(vectorId.toLong(), nombreUbicacionInicial, nombreUbicacion))

            var vectoresEnUbicacion= HibernateUbicacionDao.recuperar(nombreUbicacion).vectores
            vectoresEnUbicacion.forEach { v->if( FeedServiceImpl(FeedMongoDAO()).vectorFueContagiadoAlMover(nombreUbicacion,vectorId,v.id?.toInt()!!))
                FeedServiceImpl(FeedMongoDAO()).agregarEvento(EventoFactory.eventoPorArriboYContagio(nombreUbicacion,vectorId.toLong(),v.id!!))
            }
        }

    }
    fun moverSubTarea(vector: Vector, ubicacionNueva: Ubicacion) : Pair<List<Pair<Vector, Especie>>, List<Ubicacion>> {
        val ubicacionOrigen = HibernateUbicacionDao.recuperar(vector.ubicacion!!.nombreUbicacion)
        ubicacionOrigen.vectores.removeIf { it.id != null && it.id == vector.id } // remuevo el vector que se mueve de la ubicacion de origen
        HibernateUbicacionDao.actualizar(ubicacionOrigen)
        val infecciones = vector.contagiar(ubicacionNueva.vectores.toList()) // contagio a todos los vectores de la ubicacion nueva
        vector.ubicacion = ubicacionNueva // asigno Ubicacion de Vector
        ubicacionNueva.vectores.add(vector) // agrego el vector a la nueva ubicacion
        HibernateUbicacionDao.actualizar(ubicacionNueva)
        return Pair(infecciones, listOf(ubicacionNueva))
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
            infecciones = vectorInfectadoAleatorio.contagiar(vectoresAContagiar)
            vectoresAContagiar.forEach{vectorDao.actualizar(it)}
            infecciones.forEach {
                val tipoPatogenoDeLaEspecie = it.second.patogeno.tipo
                val nombre_de_la_especie = it.second.nombre
                val ubicacion = it.first.ubicacion
                if (ubicacion !== null && !feedService.especieYaEstabaEnLaUbicacion(ubicacion.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie, it.first.id!!))
                }
//            val especieDB = especieDAO.recuperarEspecie(it.second.id!!)
                if (especieDAO.esPandemia(it.second)) { // agregar validacion de que sea la primera vez que es pandemia
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
                }
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorInfectadoAleatorio.id!!, it.first.id!!, ubicacion?.nombreUbicacion, nombre_de_la_especie))
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
            val nombresDeUbicaciones = neo4jUbicacionDAO.moverMasCorto(vector, ubicacion)
            parInfeccionesUbicaciones = this.moverMasCortoSubtarea(vector, nombresDeUbicaciones)
            // si se ejecuta esto es porque se movio y no exploto.

            parInfeccionesUbicaciones.first.forEach {
                val tipoPatogenoDeLaEspecie = it.second.patogeno.tipo
                val nombre_de_la_especie = it.second.nombre
                val ubicacion = it.first.ubicacion
                if (ubicacion !== null && !feedService.especieYaEstabaEnLaUbicacion(ubicacion.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie, it.first.id!!))
                }
//            val especieDB = especieDAO.recuperarEspecie(it.second.id!!)
                if (especieDAO.esPandemia(it.second)) { // agregar validacion de que sea la primera vez que es pandemia
                    feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
                }
                feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorId, it.first.id!!, ubicacion?.nombreUbicacion, nombre_de_la_especie))
            }
        }
        var ubicacionOrigenActual = vector.ubicacion!!.nombreUbicacion
        parInfeccionesUbicaciones.second.forEach {
            feedService.agregarEvento(EventoFactory.eventoArribo(vectorId, ubicacionOrigenActual, it.nombreUbicacion))
            ubicacionOrigenActual = it.nombreUbicacion
        }
    }

    fun moverMasCortoSubtarea(vector: Vector, nombresDeUbicaciones: List<String>) : Pair<List<Pair<Vector, Especie>>, List<Ubicacion>> {
        var infecciones : List<Pair<Vector, Especie>> = listOf()
        var ubicaciones : List<Ubicacion> = listOf()
        nombresDeUbicaciones.forEach {
            val infeccionesUbicaciones = this.moverSubTarea(vector, HibernateUbicacionDao.recuperar(it))
            infecciones += infeccionesUbicaciones.first
            ubicaciones += infeccionesUbicaciones.second
        }
        return Pair(infecciones, ubicaciones)
    }

    override fun capacidadDeExpansion(vectorId: Long, movimientos: Int): Int {
        return TransactionRunner.addHibernate().addNeo4j().runTrx {  neo4jUbicacionDAO.capacidadDeExpansion(vectorId, movimientos) }
    }
}