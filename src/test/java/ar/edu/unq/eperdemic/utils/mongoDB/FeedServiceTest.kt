package ar.edu.unq.eperdemic.utils.mongoDB

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.FeedServiceImpl
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.FeedService
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FeedServiceTest {
    lateinit var dao : FeedMongoDAO
    lateinit var feedService : FeedService
    lateinit var ubicacionService : UbicacionService
    lateinit var patogenoService : PatogenoService
    lateinit var vectorService : VectorService
    lateinit var hibernateData : DataService

    @Before
    fun setUp(){
        dao = FeedMongoDAO()
        feedService = FeedServiceImpl(dao)
        val ubicacionDAO = HibernateUbicacionDAO()
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
        vectorService = VectorServiceImpl(HibernateVectorDAO(), ubicacionDAO)
        hibernateData = HibernateDataService()
    }

    @Test
    fun alpedirLosEventosDeContagioDeUnPatogenoNoCreadoDevuelveUNaListaVacia(){
        val result = feedService.feedPatogeno("sarasa")
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun alBuscarLosEventosDeContagioDeUnPatogenoTieneUNSoloResultadoCuandoElPatogenoSoloSeVolvioPandemiaUnaUnicaVez(){
        //Una especie se vuelve pandemia se encuentra presenta en mas de la mitad de las locaciones
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
        val result = feedService.feedPatogeno(patogenoModel.tipo )
        val eventosPandemia = result.filter{it.accionQueLoDesencadena == Accion.PATOGENO_ES_PANDEMIA.name}
        val unicoEventoPandemia = eventosPandemia.get(0)
        Assert.assertEquals(3, result.size)
        Assert.assertEquals(1, eventosPandemia.size)
        Assert.assertTrue(unicoEventoPandemia is Evento)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
    }

    @After
    fun dropAll() {
        MegalodonService().eliminarTodo()
     }
}