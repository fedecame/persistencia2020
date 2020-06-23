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
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class VectorServiceImpl(var vectorDao: VectorDAO, var ubicacionDao: UbicacionDAO) : VectorService {


    override fun contagiar(vectorInfectado: Vector, vectores: List<Vector>) {
        TransactionRunner.addHibernate().runTrx {
            vectorDao.contagiar(vectorInfectado, vectores)
        }
    }

    override fun infectar(vector: Vector, especie: Especie) {
        TransactionRunner.addHibernate().runTrx {
            vectorDao.infectar(vector,especie)
        }
        //Si lo pongo en el bloque, no termina WTF?
        val patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
        if(patogenoService.esPandemia(especie.id!!)){
            FeedServiceImpl(FeedMongoDAO()).agregarEvento(EventoFactory().eventoContagioPorPandemia(especie.patogeno.tipo))
        }
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