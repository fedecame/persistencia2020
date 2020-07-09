package ar.edu.unq.eperdemic.persistencia.dao.jdbc

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.EventoFactory
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Contagio
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Mutacion
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoPatogeno
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import ar.edu.unq.eperdemic.utils.DataService
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FeedDAOTest {
    lateinit var dao: FeedMongoDAO
    lateinit var ubicacionService: UbicacionService
    lateinit var patogenoService: PatogenoService
    lateinit var vectorService: VectorService
    lateinit var hibernateData: DataService
    lateinit var eventoFactory: EventoFactory
    lateinit var evento: Evento

    @Before
    fun setUp() {
        dao = FeedMongoDAO()
        val ubicacionDAO = HibernateUbicacionDAO()
        ubicacionService = UbicacionServiceImpl(ubicacionDAO)
        patogenoService = PatogenoServiceImpl(HibernatePatogenoDAO(), HibernateEspecieDAO())
        vectorService = VectorServiceImpl(HibernateVectorDAO(), ubicacionDAO)
        hibernateData = HibernateDataService()
        eventoFactory = EventoFactory

        evento = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.VIRUS.name, "gripe")
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
    }

    @Test
    fun seGuardaYSeRecuperaPorUnTipoDePatogenoAlMutarUnaEspecie(){
        val evento1 = eventoFactory.eventoEspecieDePatogenoMuta(TipoPatogeno.VIRUS.name, "gripe")
        dao.startTransaction()
        dao.save(evento1)
        dao.commit()
        val resultado = dao.getByTipoPatogeno(evento1.tipoPatogeno!!)
        val eventoMutar = resultado.get(1)
        Assert.assertEquals(2, resultado!!.size)
        Assert.assertTrue(eventoMutar!!.tipoEvento is Mutacion)
        Assert.assertEquals(Accion.ESPECIE_MUTADA.name, eventoMutar.accionQueLoDesencadena)
    }

    @Test
    fun seGuardaYSeRecuperaPorUnTipoDePatogenoAlCrearUnaEspecie(){
        val evento1 = eventoFactory.eventoEspecieCreada(TipoPatogeno.VIRUS.name, "gripe")
        dao.startTransaction()
        dao.save(evento1)
        dao.commit()
        val resultado = dao.getByTipoPatogeno(evento1.tipoPatogeno!!)
        val eventoCreate = resultado.get(1)
        Assert.assertEquals(2, resultado!!.size)
        Assert.assertTrue(eventoCreate!!.tipoEvento is Mutacion)

    }

    @Test
    fun alRecuperarPorUnTipoDePatogenoInexistenteRetornaListaVacia(){
        val resultado = dao.getByTipoPatogeno("Nisman")
        Assert.assertEquals(0, resultado.size)
    }

    @Test
    fun seGuardaYSeRecuperaPorTipoDePatogeno() {
        val resultado = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
        Assert.assertEquals(1, resultado.size)
    }

    @Test
    fun elEventoGuardoYRecuperadoPorElTipoDePatogenoEsConsistente(){
        val resultado = dao.getByTipoPatogeno(evento.tipoPatogeno!!)
        Assert.assertEquals(1, resultado!!.size)
        val unicoEvento = resultado.get(0)
        Assert.assertTrue(unicoEvento is Evento)
        Assert.assertNotNull(resultado)
        Assert.assertEquals(evento.tipoPatogeno, unicoEvento!!.tipoPatogeno)
        Assert.assertEquals(TipoPatogeno.VIRUS.name, unicoEvento.tipoPatogeno)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEvento.accionQueLoDesencadena)
        Assert.assertTrue(unicoEvento.tipoEvento is Contagio)
    }

    @Test
    fun seGuardaYSeRecuperaPorTipoDeEvento() {
        val evento = eventoFactory.eventoContagioPorPandemia(evento.tipoPatogeno!!, "gripe")
        dao.startTransaction()
        dao.save(evento)
        dao.commit()
        val resultSet = dao.getByTipoEvento(Contagio())
        val resultado = resultSet.get(0)
        Assert.assertTrue(resultado is Evento)
        Assert.assertNotNull(resultado)
        Assert.assertEquals(evento.tipoPatogeno, resultado!!.tipoPatogeno)
        Assert.assertEquals(TipoPatogeno.VIRUS.name, resultado!!.tipoPatogeno)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, resultado!!.accionQueLoDesencadena)
        Assert.assertTrue(resultado.tipoEvento is Contagio)
    }

    @Test
    fun alpedirLosEventosDeContagioDeUnPatogenoNoCreadoDevuelveUNaListaVacia(){
        val result = dao.feedPatogeno("sarasa")
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun alBuscarLosEventosDeContagioDeUnPatogenoTieneCuatroResultadosCuandoElPatogenoSoloSeVolvioPandemiaUnaUnicaVez(){
        this.dropAll()
        //Una especie se vuelve pandemia se encuentra presenta en mas de la mitad de las locaciones
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)

        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")
        vectorService.infectar(vectorJamaiquino, especie)
        vectorService.infectar(vectorBabilonico, especie)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
        val result = dao.feedPatogeno(TipoPatogeno.VIRUS.name)
        val pivote = dao.getByTipoPatogeno(TipoPatogeno.VIRUS.name)
        Assert.assertEquals(4, result.size)
        val unicoEvento = result.filter{it.accionQueLoDesencadena == Accion.PATOGENO_ES_PANDEMIA.name}.get(0)
        Assert.assertEquals(Accion.PATOGENO_ES_PANDEMIA.name, unicoEvento.accionQueLoDesencadena)
        Assert.assertTrue(unicoEvento.tipoEvento is Contagio)
        Assert.assertEquals(patogenoModel.tipo, unicoEvento.tipoPatogeno)
    }

    @Test
    fun alBuscarLosEventosDeContagioDeUnPatogenoQueTieneVariosEstosEstanOrdenadosEn(){
        this.dropAll()
        //Una especie se vuelve pandemia se encuentra presenta en mas de la mitad de las locaciones
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
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
        val result = dao.feedPatogeno(TipoPatogeno.VIRUS.name)
        Assert.assertEquals(4, result.size)
    }

    @Test
    fun elFeedPatogenoRetornaUnaListaDeEventosOrdenadasPorElAtributoFechaDelTipoDePatogenoIndicado(){
        this.dropAll()
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")

        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()

        vectorBabilonico.tipo= Humano()
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)
        vectorService.infectar(vectorJamaiquino, especie)
        dao.startTransaction()
        repeat(7) {
            dao.save(Evento(Contagio(), Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name, TipoPatogeno.VIRUS.name))//El mismo tipo de patogeno.
        }
        dao.commit()
        val result = dao.feedPatogeno(TipoPatogeno.VIRUS.name)
        val uno = result.get(0)
        val dos = result.get(1)
        val tres = result.get(2)
        val cuatro = result.get(3)
        val cinco = result.get(4)
        val seis = result.get(5)
        val siete = result.get(6)
        val ocho = result.get(7)
        val nueve = result.get(8)

        Assert.assertEquals(9, result.size)

        Assert.assertTrue(nueve.fecha <= ocho.fecha)
        Assert.assertTrue(ocho.fecha <= siete.fecha)
        Assert.assertTrue(siete.fecha <= seis.fecha)
        Assert.assertTrue(seis.fecha <= cinco.fecha)
        Assert.assertTrue(cinco.fecha <= cuatro.fecha)
        Assert.assertTrue(cuatro.fecha <= tres.fecha)
        Assert.assertTrue(tres.fecha <= dos.fecha)
        Assert.assertTrue(dos.fecha <= uno.fecha)
    }

    @Test
    fun alConsultarSiUnaEspecieYaEstabaEnLaUbicacionCuandoEstaNoLoEstabaDaFalse() {
        this.dropAll()
        Assert.assertFalse(dao.especieYaEstabaEnLaUbicacion("Jamaica", TipoPatogeno.VIRUS.name, "gripe"))
    }

    @Test
    fun alConsultarSiUnaEspecieYaEstabaEnLaUbicacionCuandoEstaYaTeniaUNEventoDeContagioPorPrimeraVezEnLaUbicacionDaTrue() {
        this.dropAll()
        dao.startTransaction()
        val evento = eventoFactory.eventoContagioPorPrimeraVezEnUbicacion(TipoPatogeno.VIRUS.name, "Jamaica", "gripe", 0.toLong())
        dao.save(evento)
        dao.commit()
        Assert.assertTrue(dao.especieYaEstabaEnLaUbicacion("Jamaica", TipoPatogeno.VIRUS.name, "gripe"))
    }

    @Test
    fun alConsultarSiUnaEspecieYaEstabaEnLaUbicacionCuandoEstaYaTeniaUNEventoDeDePandemiaDaFalse() {
        this.dropAll()
        dao.startTransaction()
        val evento = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.VIRUS.name, "Jamaica")
        dao.save(evento)
        dao.commit()
        Assert.assertFalse(dao.especieYaEstabaEnLaUbicacion("Jamaica", TipoPatogeno.VIRUS.name, "gripe"))
    }

    @Test
    fun alConsultarSiUnaEspecieYaEstabaEnLaUbicacionCuandoEstaYaTeniaVariosEventosDeContagioPorPrimeraVezPeroDeOtrasEspeciesEnLaUbicacionDaFalse() {
        this.dropAll()
        dao.startTransaction()
        val evento0 = eventoFactory.eventoContagioPorPrimeraVezEnUbicacion(TipoPatogeno.VIRUS.name, "New York, New York tararara", "gripe", 0.toLong())
        val evento1 = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.VIRUS.name, "Jamaica")
        val evento2 = eventoFactory.eventoContagioPorPrimeraVezEnUbicacion(TipoPatogeno.HONGO.name, "Jamaica", "gripe", 0.toLong())
        val evento3 = eventoFactory.eventoContagioPorPrimeraVezEnUbicacion(TipoPatogeno.VIRUS.name, "Jamaica", "sarampion", 0.toLong())
        val eventos = listOf(evento0, evento1, evento2, evento3)
        eventos.forEach { dao.save(it) }
        dao.commit()
        Assert.assertFalse(dao.especieYaEstabaEnLaUbicacion("Jamaica", TipoPatogeno.VIRUS.name, "gripe"))
    }

    @Test
    fun cuandoNoExisteUnEventoDePandemiaParaLaEspecieDeUnPantogenoDadoElMensajeEspecieYaEsPandemiaDelFeedDAODevuelveFalse(){
        this.dropAll()
        Assert.assertFalse(dao.especieYaTieneEventoPorPandemia("un patogeno", "una especie"))
    }

    @Test
    fun cuandoNoExisteUnEventoDePandemiaParaLaEspecieDeUnPantogenoDadoElMensajeEspecieYaEsPandemiaDelFeedDAODevuelveTrue(){
        this.dropAll()
        val jamaica = ubicacionService.crearUbicacion("Jamaica")
        val babilonia = ubicacionService.crearUbicacion("Babilonia")
        ubicacionService.crearUbicacion("NismanLandia")
        val vectorJamaiquino = Vector()
        vectorJamaiquino.ubicacion = jamaica
        vectorJamaiquino.tipo = Humano()
        val vectorBabilonico = Vector()
        vectorBabilonico.ubicacion = babilonia
        vectorBabilonico.tipo= Humano()
        vectorService.crearVector(vectorJamaiquino)
        vectorService.crearVector(vectorBabilonico)

        val patogenoModel = Patogeno()
        patogenoModel.tipo = TipoPatogeno.VIRUS.name
        val especie = patogenoService.agregarEspecie(patogenoService.crearPatogeno(patogenoModel), "gripe", "Narnia")
        vectorService.infectar(vectorJamaiquino, especie)
        vectorService.infectar(vectorBabilonico, especie)
        Assert.assertTrue(patogenoService.esPandemia(especie.id!!))
        Assert.assertTrue(dao.especieYaTieneEventoPorPandemia(patogenoModel.tipo, "gripe"))
    }

    @Test
    fun `Al tener un evento Por Pandemia de una especie no se generan eventos de Pandemias repetidos cuando esta contagia a un vector`(){
        this.dropAll()
        Assert.assertFalse(dao.especieYaTieneEventoPorPandemia(TipoPatogeno.VIRUS.name, "gripe"))
        val eventoPorPandemia = eventoFactory.eventoContagioPorPandemia(TipoPatogeno.VIRUS.name, "Jamaica")
        dao.startTransaction()
        dao.save(eventoPorPandemia)
        dao.commit()


    }

    @After
    fun dropAll() {
        MegalodonService().eliminarTodo()
    }
}