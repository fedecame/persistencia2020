package ar.edu.unq.eperdemic.utils.mongoDB

import ar.edu.unq.eperdemic.estado.Infectado
import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoPatogeno
import ar.edu.unq.eperdemic.modelo.tipoMutacion.MutacionFactorContagioInsecto
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.FeedServiceImpl
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.services.FeedService
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.tipo.Animal
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.tipo.Insecto
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
    fun alpedirLosEventosDeContagioDeUnPatogenoNoCreadoDevuelveUnaListaVacia(){
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
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("Vector_Contagia_Al_Mover", result.get(0).accionQueLoDesencadena)//Se dispara ultimo porque primero mueve y despues infecta

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

    @Test
    fun `feedVector de un vector recien creado retorna una lista vacia`() {
        val vectorNuevo = Vector()
        vectorNuevo.estado = Sano()
        vectorNuevo.tipo = Humano()
        vectorNuevo.ubicacion = ubicacionService.crearUbicacion("Jamaica")
        vectorService.crearVector(vectorNuevo)

        val listaDeEventos = feedService.feedVector(vectorNuevo.id!!)
        Assert.assertTrue(listaDeEventos.isEmpty())
        Assert.assertEquals(0, listaDeEventos.size)
    }

    @Test
    fun `mover un vector a una ubicacion sin vectores genera 1 evento de arribo feedVector`() {
        val vector = Vector()
        vector.tipo = Animal()
        vector.estado = Sano()
        vector.ubicacion = ubicacionService.crearUbicacion("Jamaica")
        vectorService.crearVector(vector)

        val fiyi = ubicacionService.crearUbicacion("Fiyi")
        ubicacionService.conectar("Jamaica", "Fiyi", "Aereo")
        ubicacionService.mover(vector.id!!.toInt(), "Fiyi")

        val eventos = feedService.feedVector(vector.id!!)
        Assert.assertEquals(1, eventos.size)
        Assert.assertEquals(Accion.ARRIBO.name, eventos.first().accionQueLoDesencadena)
    }

    @Test
    fun `mover un vector sano a una ubicacion con vectores genera 1 evento de arribo feedVector`() {
        val vector = Vector()
        vector.tipo = Animal()
        vector.estado = Sano()
        vector.ubicacion = ubicacionService.crearUbicacion("Jamaica")
        vectorService.crearVector(vector)

        val fiyi = ubicacionService.crearUbicacion("Fiyi")
        ubicacionService.conectar("Jamaica", "Fiyi", "Aereo")

        val vector1 = Vector()
        vector1.ubicacion = fiyi
        vector1.estado = Infectado()
        vector1.tipo = Insecto()
        val vector2 = Vector()
        vector2.ubicacion = fiyi
        vector2.estado = Sano()
        vector2.tipo = Humano()
        vectorService.crearVector(vector1)
        vectorService.crearVector(vector2)
        ubicacionService.mover(vector.id!!.toInt(), "Fiyi")

        val eventos = feedService.feedVector(vector.id!!)
        Assert.assertEquals(1, eventos.size)
        Assert.assertEquals(Accion.ARRIBO.name, eventos.first().accionQueLoDesencadena)
    }

    @Test
    fun `mover un vector infectado a una ubicacion con un vector sano genera 3 eventos(2 arribos y 2 contagio feedVector)`() {
        val patogeno = Patogeno()
        patogeno.tipo = "virus"
        patogeno.factorContagioHumano= 1000
        patogenoService.crearPatogeno(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "varicela", "brasil")

        val vectorInfectado = Vector()
        vectorInfectado.estado = Infectado()
        vectorInfectado.tipo = Animal()
        vectorInfectado.agregarEspecie(especie)
        vectorInfectado.ubicacion = ubicacionService.crearUbicacion("Jamaica")
        vectorService.crearVector(vectorInfectado)

        val kongo = ubicacionService.crearUbicacion("Kongo")
        ubicacionService.conectar("Jamaica", "Kongo", "Maritimo")
        val vectorSano = Vector()
        vectorSano.estado = Sano()
        vectorSano.tipo = Humano()
        vectorSano.ubicacion = kongo
        vectorService.crearVector(vectorSano)
        ubicacionService.mover(vectorInfectado.id!!.toInt(), kongo.nombreUbicacion)
        val eventosDelQueInfecta = feedService.feedVector(vectorInfectado.id!!)
        Assert.assertEquals(3, eventosDelQueInfecta.size)
        var accionesDelFeed = eventosDelQueInfecta.map{ it.accionQueLoDesencadena }
        Assert.assertTrue(accionesDelFeed.containsAll(listOf(Accion.ARRIBO.name, Accion.CONTAGIO_NORMAL.name)))

        val eventosDelQueEsInfectado = feedService.feedVector(vectorSano.id!!)
        Assert.assertEquals(3, eventosDelQueEsInfectado.size)
        accionesDelFeed = eventosDelQueEsInfectado.map{ it.accionQueLoDesencadena }
        Assert.assertTrue(accionesDelFeed.contains(Accion.CONTAGIO_NORMAL.name))
    }

    @Test
    fun `infectar un vector genera 1 evento de contagio de feedVector`() {
        val patogeno = Patogeno()
        patogeno.tipo = "virus"
        patogeno.factorContagioHumano= 1000
        patogenoService.crearPatogeno(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "varicela", "brasil")

        val vector = Vector()
        vector.tipo = Insecto()
        vector.estado = Sano()
        vector.ubicacion = ubicacionService.crearUbicacion("guadalajara")
        vectorService.crearVector(vector)
        vectorService.infectar(vector, especie)
        val eventosDePatogeno = feedService.feedPatogeno(especie.patogeno.tipo)
        Assert.assertEquals(3, eventosDePatogeno.size)
        val eventosDeContagio = feedService.feedVector(vector.id!!)
        Assert.assertEquals(1, eventosDeContagio.size)
        Assert.assertEquals(Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name, eventosDeContagio.first().accionQueLoDesencadena)
    }

    @Test
    fun `al hacer moverMasCorto y forzar la infeccion se generan tanto eventos de contagio como de arribo en feedVector`() {
        Neo4jDataService().crearSetDeDatosIniciales()
        // camino mas corto: Narnia>Ezpeleta>Babilonia
        val patogeno = Patogeno()
        patogeno.tipo = "virus"
        patogeno.factorContagioHumano= 1000
        patogenoService.crearPatogeno(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "varicela", "brasil")

        val narnia = ubicacionService.recuperarUbicacion("Narnia")
        val vectorInfectado = Vector()
        vectorInfectado.estado = Infectado()
        vectorInfectado.tipo = Animal()
        vectorInfectado.agregarEspecie(especie)
        vectorInfectado.ubicacion = narnia
        vectorService.crearVector(vectorInfectado)
        narnia.vectores.add(vectorInfectado)

        val humano1 = Vector()
        val humano2 = Vector()
        val humano3 = Vector()
        humano1.tipo = Humano()
        humano2.tipo = Humano()
        humano3.tipo = Humano()
        humano1.estado = Sano()
        humano2.estado = Sano()
        humano3.estado = Sano()
        val ezpeleta = ubicacionService.recuperarUbicacion("Ezpeleta")
        val babilonia = ubicacionService.recuperarUbicacion("Babilonia")
        humano1.ubicacion = ezpeleta
        humano2.ubicacion = ezpeleta
        humano3.ubicacion = babilonia
        vectorService.crearVector(humano1)
        vectorService.crearVector(humano2)
        vectorService.crearVector(humano3)

        ezpeleta.vectores.addAll(listOf(humano1, humano2))
        babilonia.vectores.add(humano3)
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(narnia)
            HibernateUbicacionDAO().actualizar(ezpeleta)
            HibernateUbicacionDAO().actualizar(babilonia)
        }

        ubicacionService.moverMasCorto(vectorInfectado.id!!, "Babilonia")
        val eventosFeedVector = feedService.feedVector(vectorInfectado.id!!)

        Assert.assertEquals(5, eventosFeedVector.size)
        Assert.assertEquals(2, eventosFeedVector.filter {it.accionQueLoDesencadena == Accion.ARRIBO.name}.size)
        Assert.assertEquals(3, eventosFeedVector.filter {it.accionQueLoDesencadena == Accion.CONTAGIO_NORMAL.name}.size)
    }

    @Test
    fun `al hacer expandir y forzar la infeccion se generan eventos de contagio`() {
        val patogeno = Patogeno()
        patogeno.tipo = "virus"
        patogeno.factorContagioAnimal= 1000
        patogenoService.crearPatogeno(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "gripeJ", "costa rica")

        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val vectorInfectado = Vector()
        vectorInfectado.estado = Infectado()
        vectorInfectado.tipo = Insecto()
        vectorInfectado.agregarEspecie(especie)
        vectorInfectado.ubicacion = jamaica
        vectorService.crearVector(vectorInfectado)
        jamaica.vectores.add(vectorInfectado)

        val animal1 = Vector()
        val animal2 = Vector()
        val animal3 = Vector()
        animal1.tipo = Animal()
        animal2.tipo = Animal()
        animal3.tipo = Animal()
        animal1.estado = Sano()
        animal2.estado = Sano()
        animal3.estado = Sano()
        animal1.ubicacion = jamaica
        animal2.ubicacion = jamaica
        animal3.ubicacion = jamaica
        vectorService.crearVector(animal1)
        vectorService.crearVector(animal2)
        vectorService.crearVector(animal3)
        jamaica.vectores.addAll(listOf(animal1, animal2, animal3))
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(jamaica)
        }

        ubicacionService.expandir(jamaica.nombreUbicacion)
        val eventosFeedVector = feedService.feedVector(vectorInfectado.id!!)
        Assert.assertEquals(3, eventosFeedVector.size)
        Assert.assertEquals(Accion.CONTAGIO_NORMAL.name, eventosFeedVector.first().accionQueLoDesencadena)
    }

    @Test
    fun `al hacer contagiar y forzar la infeccion se generan evidentemente eventos de contagio`() {
        val patogeno = Patogeno()
        patogeno.tipo = "virus"
        patogeno.factorContagioInsecto= 1000
        patogenoService.crearPatogeno(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "gripeN", "Japon")

        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val vectorInfectado = Vector()
        vectorInfectado.estado = Infectado()
        vectorInfectado.tipo = Humano()
        vectorInfectado.agregarEspecie(especie)
        vectorInfectado.ubicacion = jamaica
        vectorService.crearVector(vectorInfectado)
        jamaica.vectores.add(vectorInfectado)

        val insecto1 = Vector()
        val insecto2 = Vector()
        val insecto3 = Vector()
        insecto1.tipo = Insecto()
        insecto2.tipo = Insecto()
        insecto3.tipo = Insecto()
        insecto1.estado = Sano()
        insecto2.estado = Sano()
        insecto3.estado = Sano()
        insecto1.ubicacion = jamaica
        insecto2.ubicacion = jamaica
        insecto3.ubicacion = jamaica
        vectorService.crearVector(insecto1)
        vectorService.crearVector(insecto2)
        vectorService.crearVector(insecto3)
        jamaica.vectores.addAll(listOf(insecto1, insecto2, insecto3))
        TransactionRunner.addHibernate().runTrx {
            HibernateUbicacionDAO().actualizar(jamaica)
        }

        vectorService.contagiar(vectorInfectado, listOf(insecto1, insecto2, insecto3))
        val eventosFeedVector = feedService.feedVector(vectorInfectado.id!!)
        Assert.assertEquals(2, eventosFeedVector.size)
        Assert.assertEquals(Accion.CONTAGIO_NORMAL.name, eventosFeedVector.first().accionQueLoDesencadena)
    }

    @Test
    fun `Al volverse una Pandemia una especie no se generan eventos de Pandemias repetidos cuando esta contagia a un vector`(){
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("chipre")
        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")
        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        val result1 = feedService.feedPatogeno(especie.patogeno.tipo)
        val eventosPandemia1 = result1.filter { it.accionQueLoDesencadena == Accion.PATOGENO_ES_PANDEMIA.name }
        Assert.assertEquals(1, result1.size)
        Assert.assertEquals(0, eventosPandemia1.size)
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()
        Assert.assertFalse(dao.especieYaTieneEventoPorPandemia(especie.patogeno.tipo, especie.nombre))
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)
        vectorService.infectar(vectorJamaiquino, especie)
        vectorService.infectar(vectorBabilonico, especie)

        val result = feedService.feedPatogeno(especie.patogeno.tipo)
        val eventosPandemia = result.filter { it.accionQueLoDesencadena == Accion.PATOGENO_ES_PANDEMIA.name }
        val unicoEventoPandemia = eventosPandemia.get(0)
        Assert.assertEquals(4, result.size)
        Assert.assertEquals(1, eventosPandemia.size)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEventoPandemia.accionQueLoDesencadena)
        Assert.assertTrue(dao.especieYaTieneEventoPorPandemia(especie.patogeno.tipo, especie.nombre))

        val otroVectorJamaiquino = Vector()
        otroVectorJamaiquino.ubicacion = jamaica
        otroVectorJamaiquino.tipo = Humano()

        val otroVectorBabilonico = Vector()
        otroVectorBabilonico.ubicacion = babilonia
        otroVectorBabilonico.tipo = Humano()

        vectorService.crearVector(otroVectorJamaiquino)
        vectorService.crearVector(otroVectorBabilonico)
        vectorService.infectar(otroVectorJamaiquino, especie)
        vectorService.infectar(otroVectorBabilonico, especie)
        vectorService.contagiar(otroVectorJamaiquino, listOf(otroVectorBabilonico))
        vectorService.contagiar(otroVectorBabilonico, listOf(otroVectorJamaiquino))

        val result2 = feedService.feedPatogeno(especie.patogeno.tipo)
        val eventosPandemia2 = result2.filter { it.accionQueLoDesencadena == Accion.PATOGENO_ES_PANDEMIA.name }
        //Assert.assertEquals(8, result2.size)
        Assert.assertEquals(1, eventosPandemia2.size)
        Assert.assertTrue(dao.especieYaTieneEventoPorPandemia(especie.patogeno.tipo, especie.nombre))
        Assert.assertTrue(feedService.especieYaTieneEventoPorPandemia(especie.patogeno.tipo, especie.nombre))
    }

    @Test
    fun `subfuncion responde si ya hay eventos de pandemia para persistidos para un tipo de patogeno y especie dado`(){
        val patogenoModel = Patogeno()
        patogenoModel.tipo = "virus"
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")
        Assert.assertFalse(feedService.especieYaTieneEventoPorPandemia(TipoPatogeno.VIRUS.name, especie.nombre))
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        val result1 = feedService.feedPatogeno(patogenoModel.tipo)
        val eventosPandemia1 = result1.filter { it.accionQueLoDesencadena == Accion.PATOGENO_ES_PANDEMIA.name }
        Assert.assertEquals(1, result1.size)
        Assert.assertEquals(0, eventosPandemia1.size)
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()
        Assert.assertFalse(dao.especieYaTieneEventoPorPandemia(especie.patogeno.tipo, especie.nombre))
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)
        vectorService.infectar(vectorJamaiquino, especie)
        vectorService.infectar(vectorBabilonico, especie)

        Assert.assertTrue(feedService.especieYaTieneEventoPorPandemia(especie.patogeno.tipo, especie.nombre))
    }

    @After
    fun dropAll() {
        MegalodonService().eliminarTodo()
     }
}