package ar.edu.unq.eperdemic.utils.mongoDB

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jDataDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.FeedServiceImpl
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.runner.FeedService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FeedServiceTest {
    lateinit var dao : FeedMongoDAO
    lateinit var feedService :  FeedService
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
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
        val result = feedService.feedPatogeno(patogenoModel.tipo )

        val unicoEvento = result.get(0)
        Assert.assertEquals(1, result.size)
        Assert.assertTrue(unicoEvento is Evento)
    }


    @Test
    fun testUbicacion(){
        var patogeno = Patogeno()
        patogeno.tipo = ""
        patogeno.factorContagioHumano= 1000
        var especie1 = Especie()
        especie1.cantidadInfectadosParaADN = 42
        especie1.nombre = "soyUnaEspecie"
        especie1.paisDeOrigen = "Masachuset"
        especie1.patogeno = patogeno
        var vector= Vector()
        vector.agregarEspecie(especie1)
        vector.tipo=Humano()
        vector.estado= Infectado()
        var vector1= Vector()
        vector1.tipo=Humano()
        vector1.estado= Sano()
        var ubicacionCreada = ubicacionService.crearUbicacion("Florencio Varela")

        vector.ubicacion=ubicacionCreada
        vectorService.crearVector(vector)
        var vectorCreado=vectorService.recuperarVector(1)
        Assert.assertEquals(vectorCreado.ubicacion?.nombreUbicacion,"Florencio Varela")
       vector1.ubicacion= ubicacionService.crearUbicacion("Quilmes")
        vectorService.crearVector(vector1)
        ubicacionService.conectar("Florencio Varela", "Quilmes", "Terrestre")
        ubicacionService.mover(1,"Quilmes")
var vectorCreado1=vectorService.recuperarVector(vector1.id?.toInt()!!)
        val result = feedService.feedUbicacion("Quilmes")
        Assert.assertEquals(1, result.size)
                Assert.assertEquals(vector1.especies.size,vectorService.enfermedades(vector1.id?.toInt()!!).size)
    }

    @After
    fun dropAll() {
        MegalodonService().eliminarTodo()
     }
}