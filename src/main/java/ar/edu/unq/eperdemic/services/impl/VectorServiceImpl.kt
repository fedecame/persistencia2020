package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.exception.AnalisisDeSangreImposibleHacer
import ar.edu.unq.eperdemic.persistencia.dao.Redis.RedisADNDao
import ar.edu.unq.eperdemic.persistencia.dao.Redis.RedisScriptDao
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.FeedService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner


class VectorServiceImpl(var vectorDao: VectorDAO, var ubicacionDao: UbicacionDAO, var feedService : FeedService = FeedServiceImpl(FeedMongoDAO())) : VectorService {
    var redisADNDao = RedisADNDao()
    var redisScripts = RedisScriptDao()
    var antidotoServiceImpl = AntidotoServiceImpl()

    var especieDAO = HibernateEspecieDAO()

    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        val infecciones: List<Pair<Vector, Especie>> = vectorInfectado.contagiar(vectores)
        TransactionRunner.addHibernate().runTrx {
            for (vectorAContagiar in vectores) {
                vectorDao.actualizar(vectorAContagiar)
            }
        }
        this.fastForwardFeed(infecciones, vectorInfectado.id)
    }


    fun fastForwardFeed(infecciones: List<Pair<Vector, Especie>>, vectorInfectado: Long? = null) {
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
        lateinit var infeccion: List<Pair<Vector, Especie>>
        TransactionRunner.addHibernate().runTrx {
            val especieDB = HibernateEspecieDAO().recuperarEspecie(especie.id!!)
            val _vector = vectorDao.recuperar(vector.id?.toInt()!!)
            infeccion = _vector.infectarse(especieDB)
            vectorDao.actualizar(_vector)

            //crear adn de infeccion
            redisADNDao.incorporarADNDeEspecie(vector, especie)
        }
        this.fastForwardFeed(infeccion)
    }

    override fun enfermedades(vectorId: Int): List<Especie> = TransactionRunner.addHibernate().runTrx { vectorDao.enfermedades(vectorId) }

    override fun crearVector(vector: Vector): Vector = TransactionRunner.addHibernate().runTrx {
        var vector1 = vectorDao.crear(vector)
        vector1
    }

    override fun recuperarVector(vectorID: Int): Vector = TransactionRunner.addHibernate().runTrx { vectorDao.recuperar(vectorID) }


    override fun irAlMedico2(vector: Vector, especie: Especie) {
        var respuestasDeTodasLasQuerys = redisScripts.todasLasQuerys(vector, especie.nombre)
        if (respuestasDeTodasLasQuerys?.get(0) == "No se puede hacer nada") {
            throw AnalisisDeSangreImposibleHacer()
        } else {
            if (respuestasDeTodasLasQuerys != null) {
                if(respuestasDeTodasLasQuerys.get(1)=="1"){ ///se verifica si existe la cura  para esa especie
                    vector.recuperarseDeUnaEnfermedad(especie)
                    TransactionRunner.addHibernate().runTrx {
                        vectorDao.actualizar(vector)
                    }
                }
            }
        }
    }


    override fun irAlMedico(vector: Vector, especie: Especie) {
        var nombreEspecie = redisADNDao.darAdnDeEspecie(vector)
        if (redisADNDao.noExisteAdn(vector)) {
            throw AnalisisDeSangreImposibleHacer()
        } else {
            var nombreAntidoto = hacerAnalisis(especie)
            tomarAntidoto(nombreAntidoto, especie, vector)

        }
    }

        override fun hacerAnalisis(especie: Especie): String = antidotoServiceImpl.getNombreAntido(especie)!!

        override fun tomarAntidoto(antidoto: String, especie: Especie, vector: Vector) {

            if (antidotoServiceImpl.getNombreAntido(especie) == antidoto) {//se verifica si existe la cura  para esa especie
                vector.recuperarseDeUnaEnfermedad(especie)
                TransactionRunner.addHibernate().runTrx {
                    vectorDao.actualizar(vector)
                }
            }
        }

        override fun borrarVector(vectorId: Int) {
            TransactionRunner.addHibernate().runTrx {
                val vector = vectorDao.recuperar(vectorId)
                vectorDao.borrar(vector)
            }
        }

}