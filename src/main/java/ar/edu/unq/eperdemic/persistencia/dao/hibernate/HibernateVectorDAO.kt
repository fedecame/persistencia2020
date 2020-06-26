package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.exception.IDVectorNoEncontradoException
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.impl.FeedServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernateVectorDAO :  HibernateDAO<Vector>(Vector::class.java), VectorDAO  {
    val feedService = FeedServiceImpl(FeedMongoDAO())
    val especieDAO = HibernateEspecieDAO()

    override fun crear(vector : Vector): Vector {
        super.guardar(vector)
        return vector
    }
    override fun actualizar(vector:Vector){
       return super.actualizar(vector)
    }

    override fun recuperar(vectorID: Int): Vector {

        val vector = super.recuperar(vectorID.toLong())

        if(vector==null){
            throw IDVectorNoEncontradoException(vectorID)
        }
        return vector

    }

    override fun enfermedades(vectorID: Int): List<Especie> {
        val vector = this.recuperar(vectorID)
        val res = vector.especies
        return res.toList()
    }

    override fun infectar(vector: Vector, especie: Especie) : List<Pair<Vector, Especie>> {
        val _vector = this.recuperar(vector.id)
        val infeccion : List<Pair<Vector, Especie>> = _vector.infectarse(especie)
        super.actualizar(_vector)
//        if (_vector.ubicacion !== null) {
//            HibernateUbicacionDAO().actualizar(_vector.ubicacion!!)
//        }
//
//        if (infeccion.size > 0) {
//            val tipoPatogenoDeLaEspecie = infeccion.first().second.patogeno.tipo
//            val nombre_de_la_especie = infeccion.first().second.nombre
//            val ubicacion = infeccion.first().first.ubicacion
//            if (ubicacion !== null && !feedService.especieYaEstabaEnLaUbicacion(ubicacion.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
//                feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie))
//            }
////            val especieDB = especieDAO.recuperarEspecie(infeccion.first().second.id!!)
//            if (especieDAO.esPandemia(infeccion.first().second)) { // agregar validacion de que sea la primera vez que es pandemia
//                feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
//            }
//            feedService.agregarEvento(EventoFactory.eventoContagioNormal(null, infeccion.first().first.id!!, ubicacion?.nombreUbicacion))
//        }
        return infeccion
    }

    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) : List<Pair<Vector, Especie>>  {
        val infecciones: List<Pair<Vector, Especie>> = vectorInfectado.contagiar(vectores)

//        infecciones.forEach {
//            val tipoPatogenoDeLaEspecie = it.second.patogeno.tipo
//            val nombre_de_la_especie = it.second.nombre
//            val ubicacion = it.first.ubicacion
//            if (ubicacion !== null && !feedService.especieYaEstabaEnLaUbicacion(ubicacion.nombreUbicacion, tipoPatogenoDeLaEspecie, nombre_de_la_especie)) {
//                feedService.agregarEvento(EventoFactory.eventoContagioPorPrimeraVezEnUbicacion(tipoPatogenoDeLaEspecie, ubicacion.nombreUbicacion, nombre_de_la_especie))
//            }
////            val especieDB = especieDAO.recuperarEspecie(it.second.id!!)
//            if (especieDAO.esPandemia(it.second)) { // agregar validacion de que sea la primera vez que es pandemia
//                feedService.agregarEvento(EventoFactory.eventoContagioPorPandemia(tipoPatogenoDeLaEspecie, nombre_de_la_especie))
//            }
//            feedService.agregarEvento(EventoFactory.eventoContagioNormal(vectorInfectado.id!!, it.first.id!!, ubicacion?.nombreUbicacion))
//        }

        for(vectorAContagiar in vectores){
            super.actualizar(vectorAContagiar)
        }
        return infecciones
    }

    override fun borrar(vector: Vector) {
        super.eliminar(vector)
    }

}
