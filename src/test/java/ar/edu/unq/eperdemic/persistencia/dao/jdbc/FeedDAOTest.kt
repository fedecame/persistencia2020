package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.HibernateDataService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FeedDAOTest {
    lateinit var dao : FeedMongoDAO
    lateinit var ubicacionService : UbicacionService
    lateinit var patogenoService : PatogenoService
    lateinit var vectorService : VectorService
    lateinit var hibernateData : DataService
    lateinit var eventoFactory : EventoFactory
    lateinit var evento : Evento

    @Before
    fun setUp(){
        dao = FeedMongoDAO()
        val ubicacionDAO = HibernateUbicacionDAO()
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
        vectorService = VectorServiceImpl(HibernateVectorDAO(), ubicacionDAO)
        hibernateData = HibernateDataService()
        eventoFactory = EventoFactory()

        evento = eventoFactory.eventoContagioPorPandemia("Virus")
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
    }

    @Test
    fun testCommit() {
        this.dropAll()
        val resultado0 = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
        Assert.assertNull(resultado0)
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
        val resultado1 = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
        Assert.assertNotNull(resultado1)
    }


    @Test
    fun alRecuperarPorUnTipoDePatogenoInexistenteRetornaListaVacia(){
        val resultado = dao.getByTipoPatogeno("Nisman")
        Assert.assertEquals(0, resultado.size)
    }

    @Test
    fun seGuardaYSeRecuperaPorTipoDePatogeno() {
        val resultado = dao.getByTipoPatogeno(evento.tipoPatogeno!!)

    }

    @Test
    fun elEventoGuardoYRecuperadoPorElTipoDePatogenoEsConsistente(){
        val resultado = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
        Assert.assertEquals(1, resultado!!.size)
        val unicoEvento = resultado.get(0)
        Assert.assertTrue(unicoEvento is Evento)
        Assert.assertNotNull(resultado)
        Assert.assertEquals(evento.tipoPatogeno, unicoEvento!!.tipoPatogeno)
        Assert.assertEquals("Virus", unicoEvento.tipoPatogeno)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEvento.accionQueLoDesencadena)
        Assert.assertTrue(unicoEvento.tipoEvento is Contagio)
    }

    @Test
    fun seGuardaYSeRecuperaPorTipoDeEvento() {
        val evento = eventoFactory.eventoContagioPorPandemia(evento.tipoPatogeno!!)
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
        val resultSet = dao.getByTipoEvento(Contagio())
        val resultado = resultSet.get(0)
        Assert.assertTrue(resultado is Evento)
        Assert.assertNotNull(resultado)
        Assert.assertEquals(evento.tipoPatogeno, resultado!!.tipoPatogeno)
        Assert.assertEquals("Virus", resultado!!.tipoPatogeno)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, resultado!!.accionQueLoDesencadena)
        Assert.assertTrue(resultado.tipoEvento is Contagio)
    }

    @Test
    fun alpedirLosEventosDeContagioDeUnPatogenoNoCreadoDevuelveUNaListaVacia(){
        val result = dao.feedPatogeno("sarasa")
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun alBuscarLosEventosDeContagioDeUnPatogenoTieneUNSoloResultadoCuandoElPatogenoSoloSeVolvioPandemiaUnaUnicaVez(){
        //Una especie se vuelve pandemia se encuentra presenta en mas de la mitad de las locaciones
        dao.deleteAll()
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
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
        dao.startTransaction()
        val result = dao.feedPatogeno(patogenoModel.tipo)
        dao.commit()
        val pivote = dao.getByTipoPatogeno(patogenoModel.tipo)
        Assert.assertEquals(1, pivote.size)
//        val unicoEvento = result.get(0)
        Assert.assertEquals(1, result.size)
//        Assert.assertTrue(unicoEvento is Evento)
//        Assert.assertEquals("", unicoEvento.log())
    }

    @After
    fun dropAll() {
        dao.deleteAll()
        TransactionRunner.addNeo4j().addHibernate().runTrx {
            HibernateDataDAO().clear()
            Neo4jDataDAO().clear()
        }
    }
}