package ar.edu.unq.eperdemic.utils.mongoDB

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.tipoMutacion.MutacionFactorContagioInsecto
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.FeedService
import ar.edu.unq.eperdemic.services.impl.*
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
    fun alBuscarLosEventosDeMutacionDeUnPatogenoTieneUnResultadoCuandoSeMutaUnaEspecieDelPatogeno(){

        var mutacionDAO = HibernateMutacionDAO()
        var mutacionService = MutacionServiceImpl(mutacionDAO, patogenoService)

        var mutacion1 = Mutacion()
        mutacion1.adnNecesario = 1
        mutacion1.tipo = MutacionFactorContagioInsecto()
        mutacionService.crearMutacion(mutacion1)
        mutacionService.actualizarMutacion(mutacion1)

        var patogeno = Patogeno()
        patogeno.cantidadDeEspecies = 1
        patogeno.tipo = "Virus"
        patogeno.factorContagioHumano = 80
        patogeno.factorContagioInsecto = 15
        patogeno.factorContagioAnimal = 50
        patogeno.defensaContraMicroorganismos = 30
        patogeno.letalidad = 5

        var especie = Especie()
        especie.nombre = "Corona"
        especie.paisDeOrigen = "China"
        especie.mutacionesDesbloqueadas.add(mutacion1)
        especie.cantidadInfectadosParaADN = 18
        especie.patogeno = patogeno

        patogenoService.crearPatogeno(patogeno)
        patogenoService.crearEspecie(especie)

        mutacionService.mutar(especie.id!!, mutacion1.id!!.toInt())

        val result = feedService.feedPatogeno(patogeno.tipo)

        val eventosDeMutacion = result.filter { it.accionQueLoDesencadena == Accion.ESPECIE_MUTADA.name }
        val unicoEventoDeMutacion = eventosDeMutacion.get(0)
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(unicoEventoDeMutacion is Evento)
        Assert.assertEquals(Accion.ESPECIE_MUTADA.name, unicoEventoDeMutacion.accionQueLoDesencadena)
    }

    @Test
    fun alBuscarLosEventosDeMutacionDeUnPatogenoTieneUnResultadoCuandoSeCreaUnaEspecieDelPatogeno(){
        val patogenoModel = Patogeno()
        patogenoModel.tipo = "virus"
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")
        val result = feedService.feedPatogeno(patogenoModel.tipo)
        val eventosDeCreacion = result.filter { it.accionQueLoDesencadena == Accion.ESPECIE_CREADA.name }
        val unicoEventoCreacion = eventosDeCreacion.get(0)
        Assert.assertEquals(1, result.size)
        Assert.assertTrue(unicoEventoCreacion is Evento)
        Assert.assertEquals(Accion.ESPECIE_CREADA.name, unicoEventoCreacion.accionQueLoDesencadena)
    }

    @Test
    fun alBuscarLosEventosDeContagioDeUnPatogenoTieneCuatroResultadosCuandoElPatogenoSoloSeVolvioPandemiaUnaUnicaVez(){
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
        Assert.assertEquals(4, result.size)
        Assert.assertEquals(1, eventosPandemia.size)
        Assert.assertTrue(unicoEventoPandemia is Evento)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
    }


    @Test
    fun vectorInfectadoMueveAUbicacionDondeHayVectoresLosInfectaYSeDisparaEvento(){
        var patogeno = Patogeno()
        patogeno.tipo = "virus"
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
        ubicacionService.conectar("Quilmes", "Florencio Varela", "Terrestre")
        ubicacionService.mover(1,"Quilmes")
        val result = feedService.feedUbicacion("Quilmes")
        Assert.assertEquals(3, result.size)
        Assert.assertEquals("ARRIBO", result.get(0).accionQueLoDesencadena)
    }
    @Test
    fun ubicacionRecibeUnArriboYSeLanzaUnEvento() {
        var vector1= Vector()
        vector1.tipo=Humano()
        vector1.estado= Sano()
        vector1.ubicacion= ubicacionService.crearUbicacion("Florencio Varela")
        vectorService.crearVector(vector1)
        ubicacionService.crearUbicacion("Quilmes")
        ubicacionService.conectar("Florencio Varela","Quilmes","Terrestre")
        ubicacionService.mover(vector1.id?.toInt()!!,"Quilmes")
        val result = feedService.feedUbicacion("Quilmes")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(result.get(0).ubicacionDestino,"Quilmes")
    }
    @Test
    fun ubicacionRecibeDosArribos_SeLanzanDosEvento_ElUltimoQueSeLanzaEsElPrimero () {
        var vector1= Vector()
        vector1.tipo=Humano()
        vector1.estado= Sano()
        vector1.ubicacion= ubicacionService.crearUbicacion("Florencio Varela")
        var vector2= Vector()
        vector2.tipo=Humano()
        vector2.estado= Sano()
        vector2.ubicacion= ubicacionService.recuperarUbicacion("Florencio Varela")
        vectorService.crearVector(vector1)
        vectorService.crearVector(vector2)
        ubicacionService.crearUbicacion("Quilmes")
        ubicacionService.conectar("Florencio Varela","Quilmes","Terrestre")
        ubicacionService.mover(vector1.id?.toInt()!!,"Quilmes")
        ubicacionService.mover(vector2.id?.toInt()!!,"Quilmes")
        val result = feedService.feedUbicacion("Quilmes")
        Assert.assertEquals(2, result.size)
        Assert.assertEquals(2L,result.get(0).idVectorQueSeMueve)
    }

    @After
    fun dropAll() {
        MegalodonService().eliminarTodo()
     }
}