package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateVectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FeedDAOTest {
    lateinit var dao : FeedMongoDAO
    lateinit var ubicacionService : UbicacionService
    lateinit var patogenoService : PatogenoService
    lateinit var vectorService : VectorService

    @Before
    fun setUp(){
        dao = FeedMongoDAO()
        val ubicacionDAO = HibernateUbicacionDAO()
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
        vectorService = VectorServiceImpl(HibernateVectorDAO(), ubicacionDAO)
    }

    @Test
    fun alBuscarLosEventosDContagioPorConvertirseEnPandemiaDerUnTipoDePatogenoNoCreadoRetornaUnaListaVacia(){
        val result = dao.eventosDeContagioPorPandemia("sarasa")
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun alBuscarLosEventosDContagioPorConvertirseEnPandemiaDerUnTipoDePatogenoQueSiEsPandemiaRetornaUnaListaCon1Evento(){
        //Una especie se vuelve pandemia se encuentra presenta en la mitad de las locaciones
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        val patogenoModel = Patogeno()
        patogenoModel.tipo = "virus"
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")

        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)
        vectorService.infectar(vectorJamaiquino, especie)
        vectorService.infectar(vectorBabilonico, especie)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
        val result = dao.eventosDeContagioPorPandemia("virus")
        //Assert.assertEquals(1, result.size)
    }


    @After
    fun dropAll() {
        dao.deleteAll()
    }
}